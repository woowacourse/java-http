package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestParserTest {
    private static final String REQUEST = "GET /index.html HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "\n" +
            "message body";

    @Test
    void accept() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();

        //when
        httpRequestParser.accept(inputStream);

        //then
        assertAll(
                () -> assertEquals("GET /index.html HTTP/1.1", httpRequestParser.getStartLine()),
                () -> assertEquals("header", httpRequestParser.getHeader().get("header")),
                () -> assertEquals("test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46", httpRequestParser.getHeader().get("Cookie")),
                () -> assertEquals("message body", httpRequestParser.getMessageBody())
        );
    }
    
    @Test
    void findMethod() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser();

        //when
        String method = httpRequestParser.findMethod();

        //then
        assertEquals("GET", method);
    }

    private HttpRequestParser createHttpRequestParser() throws IOException {
        InputStream inputStream = new ByteArrayInputStream(REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        httpRequestParser.accept(inputStream);
        return httpRequestParser;
    }

    @Test
    void findPath() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser();

        //when
        String path = httpRequestParser.findPath();

        //then
        assertEquals("/index.html", path);
    }

    @Test
    void findProtocol() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser();

        //when
        String protocol = httpRequestParser.findProtocol();

        //then
        assertEquals("HTTP/1.1", protocol);
    }

    @Test
    void findCookies() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser();

        //when
        Map<String, String> cookies = httpRequestParser.findCookies();

        //then
        assertAll(
                () -> assertEquals("test", cookies.get("test")),
                () -> assertEquals("656cef62-e3c4-40bc-a8df-94732920ed46", cookies.get("JSESSIONID"))
        );
    }

}
