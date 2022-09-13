package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.request.TestMessage.*;
import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    @DisplayName("HttpVersion을 검증한다.")
    void getVersion() {
        // given
        final HttpMessage message = generateMessage("GET", "/path", "HTTP/1.1");
        final RequestLine requestLine = new RequestLine(message);

        // when
        final String actual = requestLine.getVersion();
        final String expected = "HTTP/1.1";

        // then
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("HttpMethod가 GET인지 검증한다(true).")
    void isGet() {
        // given
        final HttpMessage message = generateMessage("GET", "/path");
        final RequestLine requestLine = new RequestLine(message);

        // when
        final boolean actual = requestLine.isGet();

        // then
        assertThat(actual).isTrue();
    }

    @Test
    @DisplayName("HttpMethod가 GET인지 검증한다(false).")
    void isGet_false() {
        // given
        final HttpMessage message = generateMessage("POST", "/path");
        final RequestLine requestLine = new RequestLine(message);

        // when
        final boolean actual = requestLine.isGet();

        // then
        assertThat(actual).isFalse();
    }

    @Test
    @DisplayName("RequestUri를 검증한다.")
    void getUri() {
        // given
        final HttpMessage message = generateMessage("POST", "/path");
        final RequestLine requestLine = new RequestLine(message);

        // when
        final String actual = requestLine.getUri();
        final String expected = "/path";

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
