package com.suyash.se.crawler.crawler;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.suyash.se.crawler.cache.RedisUrlTrackingService;
import com.suyash.se.crawler.messaging.CrawledPagePublisher;
import com.suyash.se.crawler.messaging.CrawlingEvent;
import com.suyash.se.crawler.metrics.CrawlerMetricsService;
import com.suyash.se.crawler.service.RateLimitingService;
import com.suyash.se.crawler.service.ResilientIndexerService;

import io.micrometer.core.instrument.Timer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class WebCrawlerService {

    private final CrawledPageRepository repository;
    private final ResilientIndexerService resilientIndexerService;
    private final CrawledPagePublisher crawledPagePublisher;
    private final RedisUrlTrackingService urlTrackingService;
    private final RateLimitingService rateLimitingService;
    private final CrawlerMetricsService metricsService;

    private final String crawlerInstanceId = java.util.UUID.randomUUID().toString();

    // COMMENTED OUT - Using Redis instead of in-memory collections
    // private Queue<String> queue = new ConcurrentLinkedQueue<>();
    // private HashSet<String> visited = new HashSet<>();
    private volatile boolean isCrawling = false;

    public void addUrlToQueue(String url) {
        if (!isValidUrl(url)) {
            log.warn("Invalid URL rejected: {}", url);
            return;
        }

        if (urlTrackingService.isVisited(url)) {
            log.debug("URL already visited, skipping: {}", url);
            return;
        }

        urlTrackingService.addToQueue(url);
        log.info("Added URL to queue: {} (Queue size: {})", url, urlTrackingService.getQueueSize());

        if (!isCrawling) {
            log.info("Starting crawling process for crawler instance: {}", crawlerInstanceId);
            startCrawling();
        }
    }

    public void startCrawling() {
        new Thread(() -> {
            isCrawling = true;
            metricsService.setActiveThreads(1);
            List<CrawledPage> pages = new ArrayList<>();

            // Limiting to 100 pages for now
            while (!urlTrackingService.isQueueEmpty() && urlTrackingService.getVisitedCount() < 100) {
                String currentUrl = urlTrackingService.getNextUrl();
                if (currentUrl == null) {
                    try {
                        Thread.sleep(5000); // Wait for 5 seconds before checking again
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                    continue;
                }

                if (!urlTrackingService.isVisited(currentUrl)) {
                    // Check rate limiting before crawling
                    if (!rateLimitingService.isAllowed(currentUrl)) {
                        log.warn("Rate limit exceeded for URL: {}, skipping", currentUrl);
                        continue;
                    }

                    urlTrackingService.markAsVisited(currentUrl);
                    log.info("Crawling URL: {} (Visited: {}/100)", currentUrl, urlTrackingService.getVisitedCount());

                    // Publish crawling started event
                    crawledPagePublisher.publishCrawlingEvent(
                            CrawlingEvent.crawlingStarted(crawlerInstanceId, currentUrl));

                    Timer.Sample crawlingSample = metricsService.startCrawlingTimer();
                    CrawledPage page = crawl(currentUrl);
                    metricsService.stopCrawlingTimer(crawlingSample);

                    if (page != null) {
                        pages.add(page);
                        metricsService.recordUrlProcessed();
                        log.debug("Successfully crawled page: {} (Content length: {})",
                                currentUrl, page.getContent().length());

                        // Publish individual page if batch is getting large
                        if (pages.size() >= 10) {
                            log.info("Batch size reached 10, publishing batch");
                            buildIndex(new ArrayList<>(pages));
                            pages.clear();
                        }
                    } else {
                        metricsService.recordUrlFailed();
                        log.warn("Failed to crawl page: {}", currentUrl);
                    }
                } else {
                    log.debug("URL already visited: {}", currentUrl);
                }
            }

            if (!pages.isEmpty()) {
                buildIndex(pages);
            }
            isCrawling = false;
            metricsService.setActiveThreads(0);
            metricsService.setQueueSize(urlTrackingService.getQueueSize());
            log.info("Crawling completed for instance: {}. Total pages in final batch: {}, Total visited: {}",
                    crawlerInstanceId, pages.size(), urlTrackingService.getVisitedCount());
        }).start();
    }

    private void buildIndex(List<CrawledPage> pages) {
        if (pages.isEmpty()) {
            return;
        }

        Timer.Sample indexingSample = metricsService.startIndexingTimer();
        try {
            // Publish batch to Kafka for indexing
            crawledPagePublisher.publishCrawledPagesBatch(pages);

            // Publish completion event
            crawledPagePublisher.publishCrawlingEvent(
                    CrawlingEvent.crawlingCompleted(crawlerInstanceId, "batch", pages.size()));

            metricsService.recordPagesIndexed(pages.size());
            metricsService.stopIndexingTimer(indexingSample);
            log.info("Successfully published {} pages to Kafka for indexing", pages.size());

        } catch (Exception e) {
            log.error("Error publishing pages to Kafka: {}", e.getMessage(), e);

            // Publish failure event
            crawledPagePublisher.publishCrawlingEvent(
                    CrawlingEvent.crawlingFailed(crawlerInstanceId, "batch", e.getMessage()));

            // Fallback: try resilient indexer service
            try {
                resilientIndexerService.buildIndex(pages);
                log.info("Fallback successful: Sent {} pages via resilient indexer service", pages.size());
            } catch (Exception fallbackEx) {
                log.error("Resilient indexer service also failed: {}", fallbackEx.getMessage(), fallbackEx);
            }
        }
    }

    private CrawledPage crawl(String currentUrl) {
        log.debug("Starting to crawl: {}", currentUrl);

        try {
            Document doc = Jsoup.connect(currentUrl)
                    .timeout(10000) // 10 second timeout
                    .userAgent("SearchEngine-Crawler/1.0")
                    .get();

            // Extract title
            String title = doc.title();
            if (title == null || title.trim().isEmpty()) {
                title = "No Title";
            }

            // Extract short content (first 200 characters of the text)
            String text = doc.body().text();
            String shortContent = text.length() > 200 ? text.substring(0, 200) + "..." : text;

            CrawledPage page = saveToDatabase(currentUrl, title, shortContent, text);

            // Extract and queue new URLs
            Elements links = doc.select("a[href]");
            int newUrlsFound = 0;
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                if (!urlTrackingService.isVisited(nextUrl) && isValidUrl(nextUrl)) {
                    urlTrackingService.addToQueue(nextUrl);
                    newUrlsFound++;
                }
            }

            log.debug("Crawled: {} - Found {} new URLs, Content length: {}",
                    currentUrl, newUrlsFound, text.length());

            return page;
        } catch (Exception e) {
            log.error("Error crawling {}: {}", currentUrl, e.getMessage());
        }
        return null;
    }

    private CrawledPage saveToDatabase(String url, String title, String shortContent, String text) {
        try {
            CrawledPage page = CrawledPage.builder()
                    .url(url)
                    .content(text)
                    .shortContent(shortContent)
                    .title(title)
                    .build();
            repository.save(page);
            log.debug("Saved to database - URL: {} (ID: {})", url, page.getId());
            return page;
        } catch (Exception e) {
            log.error("Error saving page to database - URL: {}, Error: {}", url, e.getMessage());
            throw e;
        }
    }

    public boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }
}