package com.github.prasanna.webcrawler.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.StringContains.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebCrawlerControllerIntegrationTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void testGetPages_returnsHtml() {
        given()
                .queryParam("target", "https://www.example.com")
                .when()
                .get("/pages")
                .then()
                .statusCode(200)
                .body("url", equalTo("https://www.example.com"))
                .body("html", containsString("<html"));
    }
}