# Web Crawler
A simple web crawler built with Spring Boot that crawls and returns internal pages of a website. Supports configurable crawl depth, timeouts, and OpenAPI documentation.

### Features
- Crawl all pages under a given domain with configurable depth
- Depth control for recursive crawling
- Timeouts for crawl and connection to prevent long waits
- OpenAPI 3.0 documentation for easy API exploration
- Parallel crawling using configurable thread pool
- Error handling with corresponding JSON responses

### Built with
- **Spring Boot** Version 3.4.5
- **Java** Version 17
- **Maven**

### Build and Run
#### Run as Spring Boot Application :

Clone the repository

```
git clone https://github.com/Prasanna1303/webcrawler.git
```
Run the application

```
./mvnw spring-boot:run
```


##### Build and execute the jar :
Builds the project and creates the resulting webcrawler-0.0.1-SNAPSHOT.jar into 'target' folder

```
./mvnw clean install
```
Execute the jar from the target folder

```
java -jar webcrawler-0.0.1-SNAPSHOT.jar
```
By default, the service runs on http://localhost:8080


### Continuous Integration

This project uses GitHub Action for Continuous Integration (CI). The CI workflow performs the following tasks:

- Runs Maven tests to verify the code.
- Ensures code coverage using JaCoCo.
- Validates pull requests and pushes to the `main` branch.

The CI workflow file is located at `.github/workflows/ci.yml`.

### API Documentation

The API is documented using OpenAPI 3.0. You can visualize the API using the tool:

- **Swagger UI**: [View API Documentation](https://petstore.swagger.io/?url=https://raw.githubusercontent.com/Prasanna1303/webcrawler/main/src/main/resources/openapi/webcrawler-api-v1.yml)

### OpenAPI Specification

The OpenAPI specification is available in the project at `src/main/resources/openapi/webcrawler-api-v1.yml`.

### Usage
```bash
curl --location 'http://localhost:8080/pages?target=https://example.com&depth=1'
```

Response (HTTP 200 OK):
```json
{
  "domain": "https://example.com/",
  "pages": [
    "https://example.com/",
    "https://example.com/contact.html"
  ]
}
```

When the target URL is invalid, the response will be:
```bash
curl --location 'http://localhost:8080/pages?target=https:/  /example.com'
```

Response (HTTP 400 Bad Request):
```json
{
  "title": "Invalid target URL",
  "detail": "Invalid target URL: https:/  /example.com/"
}
```

### Configurable parameters:

| Parameter                     | Description                  | Default Value |
|-------------------------------|------------------------------|---------------|
| webcrawler.default-max-depth  | Maximum crawl depth          | 3             |
| webcrawler.thread-pool-size   | Thread pool size for crawling| 10            |
| webcrawler.crawl-timeout      | Crawl timeout in seconds     | 30            |
| webcrawler.connection-timeout | Connection timeout in ms     | 5000          |