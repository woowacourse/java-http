package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.message.common.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineTest {

    @DisplayName("HTTP Request Line을 전달하여 생성한다.")
    @Test
    void parse() {
        // given
        String httpRequestLine = "GET /index.html HTTP/1.1";

        // when
        RequestLine actual = RequestLine.parse(httpRequestLine);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("요청 메소드를 가져온다.")
    @Test
    void getMethod() {
        // given
        RequestLine requestLine = RequestLine.parse("GET /index.html HTTP/1.1");

        // when
        HttpMethod actual = requestLine.getMethod();

        // then
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("요청 URI를 가져온다.")
    @Test
    void getUri() {
        // given
        RequestLine requestLine = RequestLine.parse("GET /index.html HTTP/1.1");

        // when
        RequestUri actual = requestLine.getUri();

        // then
        assertThat(actual.getPath()).isEqualTo("/index.html");
    }
}
