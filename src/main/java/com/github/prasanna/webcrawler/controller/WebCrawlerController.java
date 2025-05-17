package com.github.prasanna.webcrawler.controller;

import com.github.prasanna.webcrawler.service.WebCrawlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/pages")
public class WebCrawlerController {

    private final WebCrawlerService webCrawlerService;

    public WebCrawlerController(WebCrawlerService webCrawlerService) {
        this.webCrawlerService = webCrawlerService;
    }

    @GetMapping
    public Map<String, String> getPages(@RequestParam(name = "target") String targetUrl) {
        String html = webCrawlerService.retrieveHtml(targetUrl);
        return Map.of("url", targetUrl, "html", html);
    }
}
