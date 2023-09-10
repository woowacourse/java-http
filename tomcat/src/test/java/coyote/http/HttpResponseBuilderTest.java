package coyote.http;

import org.apache.coyote.http.HttpRequest;
import org.apache.coyote.http.HttpRequestParser;
import org.apache.coyote.http.HttpResponse;
import org.apache.coyote.http.HttpResponseBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpResponseBuilderTest {

    private static final HttpRequestParser httpRequestParser = new HttpRequestParser();
    private HttpRequest httpRequest;
    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.REQUEST.getBytes());
        httpRequest = httpRequestParser.convertToHttpRequest(inputStream);
        httpResponse = new HttpResponse();
    }

    @Test
    void buildStaticFileOkResponse() throws IOException {
        //when
        HttpResponseBuilder.buildStaticFileOkResponse(httpRequest, httpResponse, httpRequest.getPath());

        //then
        String response = httpResponse.joinResponse();
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 200 OK")),
                () -> assertTrue(response.contains("Content-Type: text/html; charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 5564"))
        );
    }

    @Test
    void buildStaticFileRedirectResponse() throws IOException {
        //when
        HttpResponseBuilder.buildStaticFileRedirectResponse(httpRequest, httpResponse, httpRequest.getPath());

        //then
        String response = httpResponse.joinResponse();
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 302 FOUND")),
                () -> assertTrue(response.contains("Content-Type: text/html; charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 5564")),
                () -> assertTrue(response.contains("Location: /index.html"))
        );
    }

    @Test
    void buildStaticFileNotFoundResponse() throws IOException {
        //when
        HttpResponseBuilder.buildStaticFileNotFoundResponse(httpRequest, httpResponse);

        //then
        String response = httpResponse.joinResponse();
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 404 Not Found")),
                () -> assertTrue(response.contains("Content-Type: text/html; charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 2426"))
        );
    }

    @Test
    void buildCustomResponse() {
        //when
        HttpResponseBuilder.buildCustomResponse(httpRequest, httpResponse, "test");

        //then
        String response = httpResponse.joinResponse();
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 200 OK")),
                () -> assertTrue(response.contains("Content-Type: text/html; charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 4"))
        );
    }

}
