package com.github.prasanna.webcrawler.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class WebCrawlerServiceTest {

    @Autowired
    private WebCrawlerService webCrawlerService;

    @Test
    public void testCrawlWebsite_shouldReturnUrlsOnSameDomain() {
        String targetUrl = "http://www.example.com";
        Set<String> crawledUrls = webCrawlerService.crawlWebsite(targetUrl, null);
        assertTrue(crawledUrls.contains(targetUrl));
        assertFalse(crawledUrls.stream().anyMatch(url -> url.contains("google.com")));
    }

    @Test
    public void testCrawlWebsite_withDepthLimit0_shouldOnlyIncudeSeedPage() {
        String targetUrl = "http://www.example.com";
        Set<String> crawledUrls = webCrawlerService.crawlWebsite(targetUrl, 0);
        assertTrue(crawledUrls.contains(targetUrl));
        assertEquals(1, crawledUrls.size());
    }

    @Test
    public void testCrawlWebsite_shouldHandleParallelProcessing() {
        String targetUrl = "https://filip-ph-johansson.github.io";
        long startTime = System.currentTimeMillis();

        Set<String> crawledUrls = webCrawlerService.crawlWebsite(targetUrl, 2);
        long endTime = System.currentTimeMillis();
        assertTrue(crawledUrls.contains(targetUrl));
        assertTrue(endTime - startTime < 1000, "Crawling took too long, parallel processing might not be working");
    }
}
