package org.apache.coyote.http11.message.parser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.coyote.http11.message.request.HttpMethod;
import org.apache.coyote.http11.message.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineParserTest {

    @DisplayName("RequestLine을 파싱할 수 있다.")
    @Test
    void parseSuccess() {
        String line = "GET / HTTP/1.1";

        RequestLine requestLine = RequestLineParser.parse(line);

        assertAll(
                () -> assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getUri()).isEqualTo("/"),
                () -> assertThat(requestLine.getProtocolVersion()).isEqualTo("HTTP/1.1")
        );
    }

    @DisplayName("3자리 형태의 RequestLine가 아니면 예외가 발생한다.")
    @Test
    void parseFailure() {
        String wrongLine = "/ HTTP/1.1";

        assertThatThrownBy(() -> RequestLineParser.parse(wrongLine))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
