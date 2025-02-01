package com.suyash.search_engine_api.crawler;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

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

    private Queue<String> queue = new LinkedList<>();
    private HashSet<String> visited = new HashSet<>();

    public void crawl(String seedUrl) {
        queue.add(seedUrl);
        visited.add(seedUrl);

        while (!queue.isEmpty()) {
            String currentUrl = queue.poll();
            System.out.println("Crawling: " + currentUrl);

            try {
                // Fetch the HTML content of the page
                Document doc = Jsoup.connect(currentUrl).get();

                // Extract text from the page
                String text = doc.body().text();

                // Save the text to the database
                saveToDatabase(currentUrl, text);

                // Extract all links on the page
                Elements links = doc.select("a[href]");
                for (Element link : links) {
                    String nextUrl = link.absUrl("href");

                    // Validate the URL and check if it's already visited
                    if (!visited.contains(nextUrl) && isValidUrl(nextUrl) && !repository.existsByUrl(nextUrl) && visited.size() < 10) {
                        queue.add(nextUrl);
                        visited.add(nextUrl);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Error crawling " + currentUrl + ": " + e.getMessage());
            }
        }
    }

    private void saveToDatabase(String url, String text) {
        CrawledPage page = CrawledPage.builder()
                .url(url)
                .content(text)
                .build();
        repository.save(page);
        System.out.println("Saved to Database - URL: " + url);
    }

    private boolean isValidUrl(String url) {
        return url != null && (url.startsWith("http://") || url.startsWith("https://"));
    }

}
