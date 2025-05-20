package com.github.prasanna.webcrawler.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@Slf4j
public class WebCrawlerService {

    @Value("${webcrawler.default-max-depth}")
    private int defaultMaxDepth;

    @Value("${webcrawler.thread-pool-size}")
    private int threadPoolSize;

    private Executor executor;

    @PostConstruct
    // initializes the executor after the thead pool size is injected
    public void init() {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
    }

    public Set<String> crawlWebsite(String seedURL, Integer depth) {
        // thread-safe set to store visited URLs
        Set<String> visitedSet = ConcurrentHashMap.newKeySet();
        int maxDepth = (depth == null || depth < 0 || depth > defaultMaxDepth) ? defaultMaxDepth : depth;
        String domain = getDomain(seedURL);

        CompletableFuture<Void> crawlFuture = crawl(seedURL, visitedSet, domain, 0, maxDepth);

        // wait for the crawl to finish
        try {
            crawlFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("crawling interrupted", e);
        }

        // maintain the insertion order
        return new LinkedHashSet<>(visitedSet);
    }

    private CompletableFuture<Void> crawl(String url, Set<String> visitedSet, String domain, int depth, int maxDepth) {
        if (visitedSet.contains(url) || depth > maxDepth) {
            return CompletableFuture.completedFuture(null);
        }
        visitedSet.add(url);

        return CompletableFuture.supplyAsync(() -> getLinks(url), executor)
                .thenCompose(links -> {
                    List<CompletableFuture<Void>> futures = links.stream()
                            .filter(link -> isSameDomain(domain, link))
                            .map(link -> crawl(link, visitedSet, domain, depth + 1, maxDepth))
                            .toList();

                    return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
                });
    }

    private Set<String> getLinks(String url) {
        Set<String> fetchedLinksSet = new LinkedHashSet<>();
        try {
            Document document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");
            for (Element link: links) {
                String absUrl = link.absUrl("href");
                if (!absUrl.isEmpty()) {
                    fetchedLinksSet.add(absUrl);
                }
            }
        } catch (IOException e) {
            log.error("Error fetching links from URL: {} - ", url, e);
        }

        return fetchedLinksSet;
    }

    private boolean isSameDomain(String baseDomain, String url) {
        try {
            URI uri = new URI(url);
            return baseDomain.equalsIgnoreCase(uri.getHost());
        } catch (URISyntaxException e) {
            return false;
        }
    }

    private String getDomain(String seedURL) {
        try {
            URI uri = new URI(seedURL);
            return uri.getHost();
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URL while getting domain: " + seedURL, e);
        }
    }

}