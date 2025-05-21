package com.github.prasanna.webcrawler.controller;

import com.github.prasanna.webcrawler.model.CrawledPagesResponse;
import com.github.prasanna.webcrawler.service.WebCrawlerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/pages")
public class WebCrawlerController {

    private final WebCrawlerService webCrawlerService;

    public WebCrawlerController(WebCrawlerService webCrawlerService) {
        this.webCrawlerService = webCrawlerService;
    }

    @GetMapping
    public CrawledPagesResponse getCrawledPages(
            @RequestParam(name = "target") String targetUrl,
            @RequestParam(name = "depth", required = false) Integer depth) {
        Set<String> crawledPages = webCrawlerService.crawlWebsite(targetUrl, depth);

        return new CrawledPagesResponse(targetUrl, crawledPages);
    }
}
