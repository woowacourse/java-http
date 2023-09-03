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

    @BeforeEach
    void setUp() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.REQUEST.getBytes());
        httpRequestParser.accept(inputStream);
    }

    @Test
    void buildStaticFileOkResponse() throws IOException {
        //when
        String response = httpResponseBuilder.buildStaticFileOkResponse(httpRequestParser, httpRequestParser.findPath());

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
        String response = httpResponseBuilder.buildStaticFileRedirectResponse(httpRequestParser, httpRequestParser.findPath());

        //then
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 302 Moved Temporarily")),
                () -> assertTrue(response.contains("Content-Type: text/html;charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 5564")),
                () -> assertTrue(response.contains("Location: /index.html"))
        );
    }

    @Test
    void buildCustomResponse() {
        //when
        String response = httpResponseBuilder.buildCustomResponse(httpRequestParser, "test");

        //then
        assertAll(
                () -> assertTrue(response.contains("HTTP/1.1 200 OK")),
                () -> assertTrue(response.contains("Content-Type: text/html;charset=utf-8")),
                () -> assertTrue(response.contains("Content-Length: 4"))
        );
    }

}
