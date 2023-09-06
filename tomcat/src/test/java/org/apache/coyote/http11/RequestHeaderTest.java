package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
class RequestHeaderTest {

    private List<String> headers;

    @BeforeEach
    void setUp() {
        final String requestLine = "GET /index.html HTTP/1.1";
        final String hostHeader = "Host: localhost:8080";
        final String connectionHeader = "Connection: keep-alive";
        final String cookieHeader = "Cookie: JSESSIONID=abcd1234";

        headers = List.of(requestLine, hostHeader, connectionHeader, cookieHeader);
    }

    @Test
    void 정상적인_헤더인_경우_생성된다() {
        // given when
        final RequestHeader requestHeader = RequestHeader.from(headers);

        // then
        assertThat(requestHeader).isNotNull();
        assertThat(requestHeader.getParsedRequestURI()).isEqualTo("/index.html");
    }

    @Test
    void 파싱된_request_uri_를_반환할_수_있다() {
        // given
        final String requestLine = "GET /index?name=wuga HTTP/1.1";
        final RequestHeader requestHeader = RequestHeader.from(List.of(requestLine));

        // when
        final String parsedUri = requestHeader.getParsedRequestURI();

        // then
        assertThat(parsedUri).isEqualTo("/index");
    }

    @Test
    void 파싱되지_않은_request_uri_를_반환할_수_있다() {
        // given
        final String requestLine = "GET /index?name=wuga HTTP/1.1";
        final RequestHeader requestHeader = RequestHeader.from(List.of(requestLine));

        // when
        final String parsedUri = requestHeader.getOriginRequestURI();

        // then
        assertThat(parsedUri).isEqualTo("/index?name=wuga");
    }

    @Test
    void Http_method_를_반환할_수_있다() {
        // given
        final RequestHeader requestHeader = RequestHeader.from(headers);

        // when
        final HttpMethod httpMethod = requestHeader.getHttpMethod();

        // then
        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    void 같은_request_uri_인_경우_true를_반환한다() {
        // given when
        final RequestHeader requestHeader = RequestHeader.from(headers);

        // then
        assertThat(requestHeader.isSameParsedRequestURI("/index.html")).isTrue();
    }

    @Test
    void 다른_request_uri_인_경우_false를_반환한다() {
        // given when
        final RequestHeader requestHeader = RequestHeader.from(headers);

        // then
        assertThat(requestHeader.isSameParsedRequestURI("/different_uri")).isFalse();
    }

    @Test
    void 존재하는_header_인_경우_true를_반환한다() {
        // given when
        final RequestHeader requestHeader = RequestHeader.from(headers);

        // then
        assertThat(requestHeader.hasHeader("Connection")).isTrue();
    }

    @Test
    void 존재하지_않는_header_인_경우_false를_반환한다() {
        // given when
        final RequestHeader requestHeader = RequestHeader.from(headers);

        // then
        assertThat(requestHeader.hasHeader("Not-Exists")).isFalse();
    }

    @Test
    void 존재하는_cookie_인_경우_true를_반환한다() {
        // given when
        final RequestHeader requestHeader = RequestHeader.from(headers);

        // then
        assertThat(requestHeader.hasCookie("JSESSIONID")).isTrue();
    }

    @Test
    void 존재하지_않는_cookie_인_경우_false를_반환한다() {
        // given when
        final RequestHeader requestHeader = RequestHeader.from(headers);

        // then
        assertThat(requestHeader.hasCookie("NotExistCookie")).isFalse();
    }

    @Test
    void 존재하는_cookie_값을_반환할_수_있다() {
        // given
        final RequestHeader requestHeader = RequestHeader.from(headers);

        // when
        final String cookieValue = requestHeader.getCookie("JSESSIONID");

        // then
        assertThat(cookieValue).isEqualTo("abcd1234");
    }
}
