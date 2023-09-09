package org.apache.coyote.http11.request;

import org.apache.coyote.protocol.Protocol;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class RequestLineTest {

    @Test
    void HTTP_요청_헤더_첫_라인의_문자열을_받아서_생성한다() {
        // given
        final String firstLine = "GET /index.html HTTP/1.1 ";

        // when
        final RequestLine requestLine = RequestLine.from(firstLine);

        // then
        assertAll(() -> {
            assertThat(requestLine.getRequestPath()).isEqualTo("/index.html");
            assertThat(requestLine.getRequestMethod()).isEqualTo(RequestMethod.GET);
            assertThat(requestLine.getProtocol()).isEqualTo(Protocol.HTTP11);
        });
    }
}
