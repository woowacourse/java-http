package org.apache.coyote.http11;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpResponseBuilderTest {

    private final HttpResponseBuilder httpResponseBuilder = new HttpResponseBuilder();
    private final HttpRequestParser httpRequestParser = new HttpRequestParser();
    private HttpRequest httpRequest;

    @BeforeEach
    void setUp() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.REQUEST.getBytes());
        httpRequest = httpRequestParser.convertToHttpRequest(inputStream);
    }

    @Test
    void buildStaticFileOkResponse() throws IOException {
        //when
        String response = httpResponseBuilder.buildStaticFileOkResponse(httpRequest, httpRequest.getPath());

        //then
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 200 OK")),
                () -> assertTrue(response.contains("Content-Type: text/html;charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 5564"))
        );
    }

    @Test
    void buildStaticFileRedirectResponse() throws IOException {
        //when
        String response = httpResponseBuilder.buildStaticFileRedirectResponse(httpRequest, httpRequest.getPath());

        //then
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 302 FOUND")),
                () -> assertTrue(response.contains("Content-Type: text/html;charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 5564")),
                () -> assertTrue(response.contains("Location: /index.html"))
        );
    }

    @Test
    void buildStaticFileNotFoundResponse() throws IOException {
        //when
        String response = httpResponseBuilder.buildStaticFileNotFoundResponse(httpRequest);

        //then
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 404 Not Found")),
                () -> assertTrue(response.contains("Content-Type: text/html;charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 2426"))
        );
    }

    @Test
    void buildCustomResponse() {
        //when
        String response = httpResponseBuilder.buildCustomResponse(httpRequest, "test");

        //then
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 200 OK")),
                () -> assertTrue(response.contains("Content-Type: text/html;charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 4"))
        );
    }

}
