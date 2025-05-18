package com.github.prasanna.webcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

@Service
public class WebCrawlerService {

    public Set<String> crawlWebsite(String seedURL) {
        // maintain the order of visited URLs
        Set<String> visitedSet = new LinkedHashSet<>();
        crawl(seedURL, visitedSet, getDomain(seedURL));
        return  visitedSet;
    }

    private void crawl(String url, Set<String> visitedSet, String domain) {
        if (visitedSet.contains(url)) {
            return;
        }
        visitedSet.add(url);

        try {
            Document document = Jsoup.connect(url).get();
            Elements links = document.select("a[href]");
            for (Element link: links) {
                String absUrl = link.absUrl("href");
                if (absUrl.isEmpty()) {
                    continue;
                }

                if (isSameDomain(domain, absUrl)) {
                    crawl(absUrl, visitedSet, domain);
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
