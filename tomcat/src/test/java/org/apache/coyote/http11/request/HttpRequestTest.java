package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void 요청을_읽고_RequestLine과_RequestHeader로_분리할_수_있다() {
        // given
        String request = "GET /index.html HTTP/1.1\n"
                + "Host: localhost:8080\n"
                + "Connection: keep-alive\n";

        // when
        HttpRequest httpRequest = HttpRequest.of(request);

        // then
        assertThat(httpRequest).extracting("requestLine", "requestHeader")
                .containsExactly(RequestLine.of("GET /index.html HTTP/1.1"),
                        RequestHeader.of(List.of("Host: localhost:8080", "Connection: keep-alive")));
    }

    @Test
    void 잘못된_요청을_받으면_예외를_던진다() {
        //given
        String invalidRequest = "GET /index.html HTTP/1.1" + "Host: localhost:8080" + "Connection: keep-alive";

        // when & then
        assertThatThrownBy(() -> HttpRequest.of(invalidRequest))
                .isInstanceOf(Exception.class);
    }
}
