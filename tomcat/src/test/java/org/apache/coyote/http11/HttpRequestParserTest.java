package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestParserTest {

    @Test
    void accept() throws IOException {
        //given
        InputStream inputStream = new ByteArrayInputStream(RequestFixture.REQUEST.getBytes());
        HttpRequestParser httpRequestParser = new HttpRequestParser();

        //when
        httpRequestParser.accept(inputStream);

        //then
        assertAll(
                () -> assertEquals("GET /index.html HTTP/1.1", httpRequestParser.getMethod() + " " + httpRequestParser.getPath() + " " + httpRequestParser.getProtocol()),
                () -> assertEquals("header", httpRequestParser.getHeader().get("header")),
                () -> assertEquals("test=test; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46", httpRequestParser.getHeader().get("Cookie")),
                () -> assertEquals("message body", httpRequestParser.getMessageBody())
        );
    }
    
    @Test
    void findMethod() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser(RequestFixture.REQUEST);

        //when
        String method = httpRequestParser.getMethod();

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
        HttpRequestParser httpRequestParser = createHttpRequestParser(RequestFixture.REQUEST);

        //when
        String path = httpRequestParser.getPath();

        //then
        assertEquals("/index.html", path);
    }

    @Test
    void findProtocol() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser(RequestFixture.REQUEST);

        //when
        String protocol = httpRequestParser.getProtocol();

        //then
        assertEquals("HTTP/1.1", protocol);
    }

    @Test
    void findCookies() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser(RequestFixture.REQUEST);

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
        HttpRequestParser httpRequestParser = createHttpRequestParser(RequestFixture.REQUEST_WITH_QUERY_STRING);

        //when
        Map<String, String> queryStrings = httpRequestParser.getQueryStrings();

        //then
        assertAll(
                () -> assertEquals("account", queryStrings.get("account")),
                () -> assertEquals("password", queryStrings.get("password"))
        );
    }

    @Test
    void findPathWithoutQueryString() throws IOException {
        //given
        HttpRequestParser httpRequestParser = createHttpRequestParser(RequestFixture.REQUEST_WITH_QUERY_STRING);

        //when
        String path = httpRequestParser.getPath();

        //then
        assertEquals("/login", path);
    }
}
