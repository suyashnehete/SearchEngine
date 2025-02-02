package com.suyash.search_engine_api.crawler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WebCrawlerService {

    private final CrawledPageRepository repository;

    private Queue<String> queue = new ConcurrentLinkedQueue<>();
    private HashSet<String> visited = new HashSet<>();
    private boolean isCrawling = false;

    public void addUrlToQueue(String url) {
        if (isValidUrl(url) && !visited.contains(url)) {
            queue.add(url);
            System.out.println("Added URL to queue: " + url);
        }
        if (!isCrawling) {
            startCrawling();
        }
    }

    public void startCrawling() {
        new Thread(() -> {
            isCrawling = true;
            while (!queue.isEmpty() && visited.size() < 10) { // Limiting to 10 pages for now
                String currentUrl = queue.poll();
                if (currentUrl == null) {
                    try {
                        Thread.sleep(5000); // Wait for 5 seconds before checking again
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    continue;
                }

                if (!visited.contains(currentUrl)) {
                    visited.add(currentUrl);
                    crawl(currentUrl);
                }
            }
            isCrawling = false;
        }).start();
    }

    private void crawl(String currentUrl) {
        System.out.println("Crawling: " + currentUrl);

        try {
            Document doc = Jsoup.connect(currentUrl).get();
            // Extract title
            String title = doc.title();

            // Extract short content (first 200 characters of the text)
            String text = doc.body().text();
            String shortContent = text.length() > 200 ? text.substring(0, 200) + "..." : text;

            saveToDatabase(currentUrl, title, shortContent, text);

            Elements links = doc.select("a[href]");
            for (Element link : links) {
                String nextUrl = link.absUrl("href");
                if (!visited.contains(nextUrl) && isValidUrl(nextUrl)) {
                    queue.add(nextUrl);
                }
            }
        } catch (Exception e) {
            System.err.println("Error crawling " + currentUrl + ": " + e.getMessage());
        }
    }

    private void saveToDatabase(String url, String title, String shortContent, String text) {
        CrawledPage page = CrawledPage.builder()
                .url(url)
                .content(text)
                .shortContent(shortContent)
                .title(title)
                .build();
        repository.save(page);
        System.out.println("Saved to Database - URL: " + url);
    }

    public boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

}
