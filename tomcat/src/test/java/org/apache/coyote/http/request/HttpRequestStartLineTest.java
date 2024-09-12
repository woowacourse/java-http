package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

public class HttpRequestStartLineTest {

    @Test
    void startLine을_파싱할_수_있다() {
        // given
        String startLine = "GET /index.html HTTP/1.1";

        // when
        HttpRequestStartLine httpRequestStartLine = new HttpRequestStartLine(startLine);

        // then
        assertThat(httpRequestStartLine.getRequestURI()).isEqualTo("/index.html");
    }

    @Test
    void startLine_형식이_유효하지_않으면_예외가_발생한다() {
        // given
        String startLine = "GET /index.html";

        // when & then
        assertThatThrownBy(() -> new HttpRequestStartLine(startLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("HttpRequest의 startLine 형식이 잘못되었습니다.");
    }

    @Test
    void startLine의_httpMetohd가_지원하지_않는_httpMethod면_예외가_발생한다() {
        // given
        String startLine = "INVALID /index.html HTTP/1.1";

        // when & then
        assertThatThrownBy(() -> new HttpRequestStartLine(startLine))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("지원하지 않는 HTTP 메소드입니다.");
    }
}
