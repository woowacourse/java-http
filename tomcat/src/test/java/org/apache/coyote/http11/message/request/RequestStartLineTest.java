package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.message.common.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestStartLineTest {

    @DisplayName("HTTP Request Line을 전달하여 생성한다.")
    @Test
    void constructor() {
        // given
        String httpRequestLine = "GET /index.html HTTP/1.1";

        // when
        RequestStartLine actual = new RequestStartLine(httpRequestLine);

        // then
        assertThat(actual).isNotNull();
    }

    @DisplayName("요청 메소드를 가져온다.")
    @Test
    void getMethod() {
        // given
        RequestStartLine requestStartLine = new RequestStartLine("GET /index.html HTTP/1.1");

        // when
        HttpMethod actual = requestStartLine.getMethod();

        // then
        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @DisplayName("요청 URI를 가져온다.")
    @Test
    void getUri() {
        // given
        RequestStartLine requestStartLine = new RequestStartLine("GET /index.html HTTP/1.1");

        // when
        RequestUri actual = requestStartLine.getRequestUri();

        // then
        assertThat(actual.getPathWithoutQuery()).isEqualTo("/index.html");
    }
}
