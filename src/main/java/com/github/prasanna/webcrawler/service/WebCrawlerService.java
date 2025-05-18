package com.github.prasanna.webcrawler.service;

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
import java.util.Set;

@Service
public class WebCrawlerService {

    @Value("${webcrawler.default-max-depth}")
    private int defaultMaxDepth;

    public Set<String> crawlWebsite(String seedURL, Integer maxDepth) {
        // maintain the order of visited URLs
        Set<String> visitedSet = new LinkedHashSet<>();
        if (maxDepth == null || maxDepth < 0 || maxDepth > defaultMaxDepth) {
            maxDepth = defaultMaxDepth;
        }

        crawl(seedURL, visitedSet, getDomain(seedURL), 0, maxDepth);
        return  visitedSet;
    }

    private void crawl(String url, Set<String> visitedSet, String domain, int currentDepth, int maxDepth) {
        if (visitedSet.contains(url) || currentDepth > maxDepth) {
            return;
        }
        visitedSet.add(url);

        try {
            Document document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");
            for (Element link: links) {
                String absUrl = link.absUrl("href");
                if (!absUrl.isEmpty() && isSameDomain(domain, absUrl)) {
                    crawl(absUrl, visitedSet, domain, currentDepth + 1, maxDepth);
                }
            }
        } catch (IOException e) {
            // handle errors gracefully (404, timeout, etc.)
            throw new RuntimeException(e);
        }
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
