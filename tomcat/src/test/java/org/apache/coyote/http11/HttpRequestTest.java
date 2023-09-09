package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @DisplayName("Accept 헤더가 포함되어 있는지 확인한다.")
    @Test
    void containsAccept() {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(
            Map.of(HttpHeaderName.ACCEPT, "text/html")
        );

        // when
        final HttpRequest httpRequestContainsAccept = new HttpRequest(httpHeaders, HttpMethod.GET,
            HttpRequestURI.from("/"), "HTTP/1.1",
            Map.of());

        final HttpRequest httpRequestNotContainsAccept = new HttpRequest(new HttpHeaders(), HttpMethod.GET,
            HttpRequestURI.from("/"), "HTTP/1.1",
            Map.of());

        // then
        assertAll(
            () -> assertThat(httpRequestContainsAccept.containsAccept("text/html")).isTrue(),
            () -> assertThat(httpRequestContainsAccept.containsAccept("application/json")).isFalse(),
            () -> assertThat(httpRequestNotContainsAccept.containsAccept("text/html")).isFalse()
        );
    }

    @DisplayName("쿼리 스트링이 존재하는지 확인한다.")
    @Test
    void hasQueryString() {
        // given
        final HttpRequest httpRequestContainsQueryString = new HttpRequest(new HttpHeaders(), HttpMethod.GET,
            HttpRequestURI.from("/login?account=gugu&password=password"), "HTTP/1.1",
            Map.of());

        final HttpRequest httpRequestNotContainsQueryString = new HttpRequest(new HttpHeaders(), HttpMethod.GET,
            HttpRequestURI.from("/"), "HTTP/1.1",
            Map.of());

        // when
        // then
        assertAll(
            () -> assertThat(httpRequestContainsQueryString.hasQueryString()).isTrue(),
            () -> assertThat(httpRequestNotContainsQueryString.hasQueryString()).isFalse()
        );
    }

    @DisplayName("Content Type 이 존재하는지 확인한다.")
    @Test
    void containsContentType() {
        // given
        final HttpHeaders httpHeadersWithContentType = new HttpHeaders(
            Map.of(HttpHeaderName.CONTENT_TYPE, "text/html")
        );

        final HttpHeaders httpHeadersNotIncludeContentType = new HttpHeaders(
            Map.of(HttpHeaderName.ACCEPT, "text/html")
        );

        // when
        final HttpRequest httpRequestContainsContentType = new HttpRequest(httpHeadersWithContentType, HttpMethod.GET,
            HttpRequestURI.from("/"), "HTTP/1.1",
            Map.of());

        final HttpRequest httpRequestNotContainsContentType = new HttpRequest(httpHeadersNotIncludeContentType,
            HttpMethod.GET,
            HttpRequestURI.from("/"), "HTTP/1.1",
            Map.of());

        // then
        assertAll(
            () -> assertThat(httpRequestContainsContentType.containsContentType("text/html")).isTrue(),
            () -> assertThat(httpRequestContainsContentType.containsContentType("application/json")).isFalse(),
            () -> assertThat(httpRequestNotContainsContentType.containsContentType("text/html")).isFalse()
        );
    }

    @DisplayName("Cookie 와 JSession ID 를 갖는지 확인한다.")
    @Test
    void containsCookieAndJSessionID() {
        // given
        final HttpHeaders httpHeadersWithCookieAndJSessionID = new HttpHeaders(
            Map.of(HttpHeaderName.COOKIE, "JSESSIONID=1234")
        );

        final HttpHeaders httpHeadersNotIncludeCookie = new HttpHeaders(
            Map.of(HttpHeaderName.ACCEPT, "text/html")
        );

        final HttpHeaders httpHeadersNotJsessionIDButCookie = new HttpHeaders(
            Map.of(HttpHeaderName.COOKIE, "Nothing=1234")
        );

        // when
        final HttpRequest httpRequestCookieJSessionID = new HttpRequest(httpHeadersWithCookieAndJSessionID,
            HttpMethod.GET,
            HttpRequestURI.from("/"), "HTTP/1.1",
            Map.of());

        final HttpRequest httpRequestNoCookie = new HttpRequest(httpHeadersNotIncludeCookie,
            HttpMethod.GET,
            HttpRequestURI.from("/"), "HTTP/1.1",
            Map.of());

        final HttpRequest httpRequestNotJSessionID = new HttpRequest(httpHeadersNotJsessionIDButCookie,
            HttpMethod.GET,
            HttpRequestURI.from("/"), "HTTP/1.1",
            Map.of());

        // then
        assertAll(
            () -> assertThat(httpRequestCookieJSessionID.containsCookieAndJSessionID()).isTrue(),
            () -> assertThat(httpRequestNoCookie.containsCookieAndJSessionID()).isFalse(),
            () -> assertThat(httpRequestNotJSessionID.containsCookieAndJSessionID()).isFalse()
        );
    }

    @DisplayName("가지고 있는 쿠키 값으로 쿠키를 생성한다.")
    @Test
    void getCookie() {
        // given
        final HttpHeaders httpHeaders = new HttpHeaders(
            Map.of(HttpHeaderName.COOKIE, "JSESSIONID=12395712893")
        );

        final HttpRequest httpRequest = new HttpRequest(httpHeaders, HttpMethod.GET,
            HttpRequestURI.from("/"), "HTTP/1.1",
            Map.of());

        // when
        final HttpCookie httpCookie = httpRequest.getCookie();

        // then
        assertThat(httpCookie.getJSessionID()).isEqualTo("12395712893");
    }
}
