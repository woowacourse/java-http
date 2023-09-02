package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestLineTest {

    @Test
    void 올바르지_않은_RequestLine_형식을_입력받는_경우_InvalidRequestLineException_예외를_던진다() {
        // given
        final String line = "GET /index.html HTTP/1.1 hello";

        // expect
        assertThatThrownBy(() -> RequestLine.from(line))
                .isInstanceOf(InvalidRequestLineException.class)
                .hasMessage("올바르지 않은 RequestLine 형식입니다.");
    }

    @Test
    void 올바른_RequestLine_형식인_경우_RequestLine_객체가_정상적으로_생성된다() {
        // given
        final String line = "GET /index.html HTTP/1.1";

        // when
        final RequestLine requestLine = RequestLine.from(line);

        // then
        assertAll(
                () -> assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(requestLine.getUri()).isEqualTo("/index.html"),
                () -> assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1")
        );
    }
}
