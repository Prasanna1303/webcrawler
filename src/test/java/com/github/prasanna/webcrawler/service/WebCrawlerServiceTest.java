package com.github.prasanna.webcrawler.service;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebCrawlerServiceTest {

    private final WebCrawlerService webCrawlerService = new WebCrawlerService();

    @Test
    public void testCrawlWebsite_shouldReturnUrlsOnSameDomain() {
        String targetUrl = "http://www.example.com";
        Set<String> crawledUrls = webCrawlerService.crawlWebsite(targetUrl);
        assertTrue(crawledUrls.contains(targetUrl));
        assertFalse(crawledUrls.stream().anyMatch(url -> url.contains("google.com")));
    }
}
