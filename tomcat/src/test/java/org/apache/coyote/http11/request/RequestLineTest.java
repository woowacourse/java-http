package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestLineTest {

    @Test
    void requestLine을_입력해_생성한다() {
        // given
        String requestLine = "GET /index.html HTTP/1.1 ";

        // when
        RequestLine line = RequestLine.from(requestLine);

        // then
        assertThat(line).isEqualTo(
                new RequestLine("GET", "/index.html", "HTTP/1.1")
        );
    }
}
