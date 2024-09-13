package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.Http11Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestCookieParserTest {

    private final Http11RequestCookieParser requestCookieParser = new Http11RequestCookieParser(
            new Http11StartLineParser());

    @Test
    @DisplayName("한개의 쿠키를 잘 파싱하는지 확인")
    void parseCookie() {
        String requestMessage = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                Cookie: first=1;\r
                \r
                """;
        List<Http11Cookie> http11Cookies = requestCookieParser.parseCookies(requestMessage);

        assertThat(http11Cookies)
                .containsExactly(new Http11Cookie("first", "1"));
    }

    @Test
    @DisplayName("여러개의 쿠키를 잘 파싱하는지 확인")
    void parseCookies() {
        String requestMessage = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                Cookie: first=1;second=2\r
                \r
                """;
        List<Http11Cookie> http11Cookies = requestCookieParser.parseCookies(requestMessage);

        assertThat(http11Cookies)
                .containsExactly(new Http11Cookie("first", "1"), new Http11Cookie("second", "2"));
    }

    @Test
    @DisplayName("쿠키가 없을 때 잘 동작하는지 확인")
    void parseCookiesWhenCookieIsEmpty() {
        String requestMessage = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                \r
                """;
        List<Http11Cookie> http11Cookies = requestCookieParser.parseCookies(requestMessage);

        assertThat(http11Cookies).isEmpty();
    }
}
