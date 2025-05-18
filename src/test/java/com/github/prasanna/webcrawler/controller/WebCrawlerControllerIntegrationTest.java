package com.github.prasanna.webcrawler.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.core.IsEqual.equalTo;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WebCrawlerControllerIntegrationTest {

    @BeforeAll
    static void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void testGetCrawledPages_shouldReturnPageList() {
        given()
                .queryParam("target", "https://www.example.com")
                .when()
                .get("/pages")
                .then()
                .statusCode(200)
                .body("domain", equalTo("https://www.example.com"))
                .body("pages[0]", equalTo("https://www.example.com"));
    }
}