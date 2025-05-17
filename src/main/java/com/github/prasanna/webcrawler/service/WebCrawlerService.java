package com.github.prasanna.webcrawler.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class WebCrawlerService {

    public String retrieveHtml(String url) {
        try {
            // download the HTML document from the given URL
            Document document = Jsoup.connect(url).get();
            return document.html();

        } catch (IOException e) {
            throw new RuntimeException("Failed to retrieve HTML from URL: " + url, e);
        }
    }
}
