package org.apache.coyote.http11;

import org.apache.coyote.http11.enums.HttpMethod;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {

    @Test
    void 요청_라인에서_HTTP_메서드와_경로와_버전을_분리한다() {
        final RequestLine requestLine = new RequestLine("GET /index HTTP/1.1");

        assertThat(requestLine.getHttpMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getPath()).isEqualTo("/index");
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void 요청_대상의_쿼리_파라미터를_디코딩한다() {
        final RequestLine requestLine = new RequestLine(
                "GET /search?keyword=java%20http&page=1 HTTP/1.1"
        );

        assertThat(requestLine.getPath()).isEqualTo("/search");
        assertThat(requestLine.getParams())
                .containsEntry("keyword", "java http")
                .containsEntry("page", "1");
    }

    @Test
    void 요청_라인이_세_부분으로_구성되지_않으면_예외가_발생한다() {
        assertThatThrownBy(() -> new RequestLine("GET /index"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잘못된 요청 줄");
    }
}
