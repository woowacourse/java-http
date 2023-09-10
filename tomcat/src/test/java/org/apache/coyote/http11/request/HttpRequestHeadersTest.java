package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.common.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestHeadersTest {

    private final List<String> REQUEST_HEADERS_WITHOUT_COOKIE = List.of(
            "Host: www.example.com",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
            "Accept-Language: en-US,en;q=0.5",
            "Connection: keep-alive"
    );
    private final List<String> REQUEST_HEADERS_WITH_COOKIE = List.of(
            "Host: www.example.com",
            "Accept: text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8",
            "Accept-Language: en-US,en;q=0.5",
            "Connection: keep-alive",
            "Cookie: JSESSIONID=TEST-JSESSIONID"
    );

    @Test
    @DisplayName("입력 문자열로부터 RequestHeader를 추출해낼 수 있다.")
    void createHttpRequestHeadersWithoutCookie() {
        //given
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(REQUEST_HEADERS_WITHOUT_COOKIE);

        assertThat(httpRequestHeaders.hasCookie()).isFalse();
        assertThat(httpRequestHeaders.get("Host")).isEqualTo("www.example.com");
        assertThat(httpRequestHeaders.get("Accept"))
                .isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
    }

    @Test
    @DisplayName("입력 문자열로부터 RequestHeader를 추출해낼 수 있다.")
    void createHttpRequestHeadersWithCookie() {
        //given
        HttpRequestHeaders httpRequestHeaders = HttpRequestHeaders.from(REQUEST_HEADERS_WITH_COOKIE);

        assertThat(httpRequestHeaders.hasCookie()).isTrue();
        assertThat(httpRequestHeaders.get("Host")).isEqualTo("www.example.com");
        assertThat(httpRequestHeaders.get("Accept"))
                .isEqualTo("text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
        Cookie jSessionCookie = httpRequestHeaders.getCookie("JSESSIONID");
        assertThat(jSessionCookie.getValue()).isEqualTo("TEST-JSESSIONID");
    }

}