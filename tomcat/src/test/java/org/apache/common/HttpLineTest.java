package org.apache.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpLineTest {

    @Test
    void HTTP_요청에서_첫_라인을_생성한다() {
        String firstLine = "GET /index.html HTTP/1.1 ";

        HttpLine httpLine = HttpLine.of(firstLine);

        assertAll(
                () -> assertThat(httpLine.getHttpMethod()).isEqualTo(HttpMethod.GET),
                () -> assertThat(httpLine.getRequestTarget()).isEqualTo("/index.html"),
                () -> assertThat(httpLine.getHttpVersion()).isEqualTo("HTTP/1.1")
        );
    }

    @Test
    void 올바르지_않은_HTTP_요청이면_예외가_발생한다() {
        String firstLine = "/index.html HTTP/1.1 ";

        assertThatThrownBy(() -> HttpLine.of(firstLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("올바르지 않은 HTTP 요청입니다.");
    }
}
