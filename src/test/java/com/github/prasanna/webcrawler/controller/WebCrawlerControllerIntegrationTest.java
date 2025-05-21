package com.github.prasanna.webcrawler.controller;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
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
        String targetUrl = "https://www.example.com";
        given()
                .queryParam("target", targetUrl)
                .when()
                .get("/pages")
                .then()
                .statusCode(200)
                .body("domain", equalTo(targetUrl))
                .body("pages[0]", equalTo(targetUrl));
    }

    @Test
    void testGetCrawledPages_withDepth0_shouldReturnOnlySeedPage() {
        String targetUrl = "https://www.example.com";
        given()
                .queryParam("target", targetUrl)
                .queryParam("depth", "0")
                .when()
                .get("/pages")
                .then()
                .statusCode(200)
                .body("domain", equalTo(targetUrl))
                .body("pages.size()", equalTo(1));
    }

    @Test
    void testGetCrawledPages_withInvalidUrl_shouldReturnBadRequest() {
        String invalidUrl = "https:/ /www.example.com";
        given()
                .queryParam("target", invalidUrl)
                .when()
                .get("/pages")
                .then()
                .statusCode(400)
                .body("title", equalTo("Invalid target URL"));
    }

    @Test
    void testGetCrawledPages_withUrlWithoutDomain_shouldReturnBadRequest() {
        String invalidUrl = "invalid-url";
        given()
                .queryParam("target", invalidUrl)
                .when()
                .get("/pages")
                .then()
                .statusCode(400)
                .body("title", equalTo("Invalid target URL"))
                .body("detail", containsString("URL does not contain a valid domain"));
    }
}