package com.github.prasanna.webcrawler.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WebCrawlerServiceTest {

    private final WebCrawlerService webCrawlerService = new WebCrawlerService();

    @Test
    public void testRetrieveHtml_shouldReturnHtml() {
        String targetUrl = "http://www.example.com";
        String html = webCrawlerService.retrieveHtml(targetUrl);
        assertNotNull(html);
        assertTrue(html.contains("<html>"));
    }
}
