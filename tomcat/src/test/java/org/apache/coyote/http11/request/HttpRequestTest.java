package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.InvalidRequestLineException;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 평문_요청으로_HttpRequest를_생성한다() {
        // given
        String rawRequest = "GET /index.html HTTP/1.1\r\n" +
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";

        // when & then
        assertThatCode(() -> HttpRequest.of(rawRequest))
                .doesNotThrowAnyException();
    }

    @Test
    void 요청_라인이_유효하지_않으면_InvalidRequestLineException을_던진다() {
        // given
        String rawRequest = "GET /index.html\r\n" + // HTTP/1.1 누락
                "Host: localhost:8080\r\n" +
                "Connection: keep-alive\r\n" +
                "\r\n";

        // when & then
        assertThatThrownBy(() -> HttpRequest.of(rawRequest))
                .isInstanceOf(InvalidRequestLineException.class);
    }
}
