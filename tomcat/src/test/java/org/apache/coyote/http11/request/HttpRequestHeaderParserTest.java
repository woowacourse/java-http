package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.Http11Header;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderParserTest {

    private final Http11RequestHeaderParser requestHeaderParser = new Http11RequestHeaderParser(
            new Http11StartLineParser());

    @Test
    @DisplayName("하나의 헤더를 잘 파싱하는지 확인")
    void parseHeader() {
        String requestMessage = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                \r
                """;
        List<Http11Header> http11Cookies = requestHeaderParser.parseHeaders(requestMessage);

        assertThat(http11Cookies).containsExactly(new Http11Header("Host", "localhost:8080"));
    }

    @Test
    @DisplayName("여러개의 헤더를 잘 파싱하는지 확인")
    void parseHeaders() {
        String requestMessage = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Connection: keep-alive\r
                Accept: */*\r
                \r
                """;
        List<Http11Header> http11Cookies = requestHeaderParser.parseHeaders(requestMessage);

        assertThat(http11Cookies)
                .containsExactly(
                        new Http11Header("Host", "localhost:8080"),
                        new Http11Header("Connection", "keep-alive"),
                        new Http11Header("Accept", "*/*")
                );
    }

    @Test
    @DisplayName("Cookie 헤더는 제외하고 파싱하는지 확인")
    void parseHeaderWithOutCookie() {
        String requestMessage = """
                GET /index.html HTTP/1.1\r
                Host: localhost:8080\r
                Cookie: first=1;\r
                \r
                """;
        List<Http11Header> http11Cookies = requestHeaderParser.parseHeaders(requestMessage);

        assertThat(http11Cookies).containsExactly(new Http11Header("Host", "localhost:8080"));
    }
}
