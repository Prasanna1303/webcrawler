package com.github.prasanna.webcrawler.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class CrawledPagesResponse {
    private String domain;
    private Set<String> pages;
}
