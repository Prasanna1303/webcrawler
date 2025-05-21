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
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class WebCrawlerService {

    @Value("${webcrawler.default-max-depth}")
    private int defaultMaxDepth;

    @Value("${webcrawler.thread-pool-size}")
    private int threadPoolSize;

    @Value("${webcrawler.crawl-timeout}")
    private int crawlTimeout;

    @Value("${webcrawler.connection-timeout}")
    private int connectionTimeout;

    private Executor executor;

    @PostConstruct
    // initializes the executor after the thread pool size is injected
    public void init() {
        this.executor = Executors.newFixedThreadPool(threadPoolSize);
        log.info("WebCrawler service initialized with thread pool size: {}", threadPoolSize);
    }

    public Set<String> crawlWebsite(String seedURL, Integer depth) {
        log.info("Started Crawling for target URL: {}", seedURL);
        // thread-safe set to store visited URLs
        Set<String> visitedSet = ConcurrentHashMap.newKeySet();
        int maxDepth = (depth == null || depth < 0 || depth > defaultMaxDepth) ? defaultMaxDepth : depth;

        log.info("Crawling depth set to: {}", maxDepth);

        String domain = getDomain(seedURL);

        CompletableFuture<Void> crawlFuture = crawl(seedURL, visitedSet, domain, 0, maxDepth)
                .orTimeout(crawlTimeout, TimeUnit.SECONDS); // set timeout for the crawl

        // wait for the crawl to finish
        try {
            crawlFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Crawling interrupted for target URL: {}", seedURL, e);
            throw new RuntimeException("crawling interrupted", e);
        }

        log.info("Crawling completed for target URL: {} with {} links", seedURL, visitedSet.size());
        return new LinkedHashSet<>(visitedSet);
    }

    private CompletableFuture<Void> crawl(String url, Set<String> visitedSet, String domain, int depth, int maxDepth) {
        if (visitedSet.contains(url) || depth > maxDepth) {
            return CompletableFuture.completedFuture(null);
        }

        log.info("Crawling URL: {} at depth: {}", url, depth);
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
            log.debug("Getting links for URL: {}", url);
            Document document = Jsoup.connect(url)
                    .timeout(connectionTimeout) // timeout to prevent long waits
                    .get();
            Elements links = document.select("a[href]");
            for (Element link: links) {
                String absUrl = link.absUrl("href");
                if (!absUrl.isEmpty()) {
                    fetchedLinksSet.add(absUrl);
                }
            }
            log.debug("Fetched {} links from URL: {}", fetchedLinksSet.size(), url);
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
            log.warn("Invalid URL encountered while checking for same domain: {} - ", url, e);
            return false;
        }
    }

    private String getDomain(String seedURL) {
        try {
            URI uri = new URI(seedURL);
            String domain = uri.getHost();
            if (domain == null) {
                throw new IllegalArgumentException("URL does not contain a valid domain: " + seedURL);
            }

            return uri.getHost();
        } catch (URISyntaxException e) {
            log.error("Invalid target URL: {} - ", seedURL, e);
            throw new IllegalArgumentException("Invalid target URL: " + seedURL, e);
        }
    }

}