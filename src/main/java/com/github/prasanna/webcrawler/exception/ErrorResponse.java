package com.github.prasanna.webcrawler.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    // A short, summary of the problem type
    private String title;
    // A short, human-readable summary of the problem
    private String detail;
}