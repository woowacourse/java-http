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
            "Content-Length: 12\n" +
            "\n" +
            "message body";
    private static final String REQUEST_WITH_QUERY_STRING = "GET /login?account=account&password=password HTTP/1.1\n" +
            "header: header\n" +
            "Cookie: test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46\n" +
            "Content-Length: 12\n" +
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
        HttpRequestParser httpRequestParser = createHttpRequestParser(REQUEST);

        //when
        String method = httpRequestParser.findMethod();

        //then
        assertEquals("GET", method);
    }

    private HttpRequestParser createHttpRequestParser(String request) throws IOException {
        InputStream inputStream = new ByteArrayInputStream(request.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();
        httpRequestParser.accept(inputStream);
        return httpRequestParser;
    }

    @Test
    void findPath() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser(REQUEST);

        //when
        String path = httpRequestParser.findPath();

        //then
        assertEquals("/index.html", path);
    }

    @Test
    void findProtocol() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser(REQUEST);

        //when
        String protocol = httpRequestParser.findProtocol();

        //then
        assertEquals("HTTP/1.1", protocol);
    }

    @Test
    void findCookies() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser(REQUEST);

        //when
        Map<String, String> cookies = httpRequestParser.findCookies();

        //then
        assertAll(
                () -> assertEquals("test", cookies.get("test")),
                () -> assertEquals("656cef62-e3c4-40bc-a8df-94732920ed46", cookies.get("JSESSIONID"))
        );
    }

    @Test
    void findQueryStrings() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser(REQUEST_WITH_QUERY_STRING);

        //when
        Map<String, String> queryStrings = httpRequestParser.findQueryStrings();

        //then
        assertAll(
                () -> assertEquals("account", queryStrings.get("account")),
                () -> assertEquals("password", queryStrings.get("password"))
        );
    }

    @Test
    void findPathWithoutQueryString() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser(REQUEST_WITH_QUERY_STRING);

        //when
        String path = httpRequestParser.findPathWithoutQueryString();

        //then
        assertEquals("/login", path);
    }
}
