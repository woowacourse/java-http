package org.apache.coyote.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.cookie.Cookie;
import org.apache.coyote.cookie.Cookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("쿼리를 제외하고 자원의 확장자를 포함한 URI 를 가져올 수 있다.")
    @Test
    void getRequestUrlWithoutQuery() {
        // given
        String startLine = "GET /login?account=dwoo&password=123 HTTP1.1 ";
        final HttpRequest sut = HttpRequest.of(startLine, HttpHeader.from(Map.of()), HttpRequestBody.from(""));

        // when
        final String url = sut.getRequestPath();

        // then
        assertThat(url).isEqualTo("/login.html");
    }

    @DisplayName("확장자를 추가하고, 쿼리스트링을 제외한 URI 를 가져올 수 있다.")
    @Test
    void getRequestUrl() {
        // given
        String startLine = "GET /login?account=dwoo&password=123 HTTP1.1 ";
        final HttpRequest sut = HttpRequest.of(startLine, HttpHeader.from(Map.of()), HttpRequestBody.from(""));

        // when
        final String requestPath = sut.getRequestPath();

        // then
        assertThat(requestPath).isEqualTo("/login.html");
    }

    @DisplayName("헤더에서 쿠키 정보를 조회할 수 있다.")
    @Test
    void getCookies() {
        // given
        String startLine = "GET /index.html HTTP/1.1 ";
        final HttpRequest sut = HttpRequest.of(startLine, HttpHeader.from(Map.of(
                "Host", "localhost:8080",
                "Connection", "keep-alive",
                "Accept", "*/*",
                "Cookie", "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        )), HttpRequestBody.from(""));

        // when
        final Cookies cookies = sut.getCookies();

        // then
        final Cookie yummy_cookie = cookies.getCookie("yummy_cookie").orElseThrow();
        final Cookie tasty_cookie = cookies.getCookie("tasty_cookie").orElseThrow();
        final Cookie JSESSIONID = cookies.getCookie("JSESSIONID").orElseThrow();

        assertThat(yummy_cookie.toHeaderFormat()).isEqualTo("yummy_cookie=choco");
        assertThat(tasty_cookie.toHeaderFormat()).isEqualTo("tasty_cookie=strawberry");
        assertThat(JSESSIONID.toHeaderFormat()).isEqualTo("JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
