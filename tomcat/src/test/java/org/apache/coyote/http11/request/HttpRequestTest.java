package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.HttpCookie;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpRequestTest {

    @Test
    void isGet_메서드는_GET_요청인_경우_true를_반환한다() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1 ");
        final HttpRequest httpRequest = new HttpRequest(requestLine, null, null);

        // expect
        assertThat(httpRequest.isGet()).isTrue();
    }

    @Test
    void isGet_메서드는_GET_요청이_아닌_경우_false를_반환한다() {
        // given
        final RequestLine requestLine = RequestLine.from("POST /register HTTP/1.1 ");
        final HttpRequest httpRequest = new HttpRequest(requestLine, null, null);

        // expect
        assertThat(httpRequest.isGet()).isFalse();
    }

    @Test
    void isPost_메서드는_Post_요청인_경우_true를_반환한다() {
        // given
        final RequestLine requestLine = RequestLine.from("POST /register HTTP/1.1 ");
        final HttpRequest httpRequest = new HttpRequest(requestLine, null, null);

        // expect
        assertThat(httpRequest.isPost()).isTrue();
    }

    @Test
    void isPost_메서드는_Post_요청이_아닌_경우_true를_반환한다() {
        // given
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1 ");
        final HttpRequest httpRequest = new HttpRequest(requestLine, null, null);

        // expect
        assertThat(httpRequest.isPost()).isFalse();
    }

    @Test
    void Cookie를_반환한다() {
        // given
        final Headers headers = new Headers();
        headers.addHeader("Cookie: yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
        final HttpRequest httpRequest = new HttpRequest(null, headers, null);

        // when
        final HttpCookie httpCookie = httpRequest.parseCookie();

        // then
        assertThat(httpCookie.getItems()).contains(
                entry("yummy_cookie", "choco"),
                entry("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }
}
