package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void 요청_라인에서_메서드와_경로와_버전을_파싱한다() {
        RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void 쿼리스트링은_경로와_분리해_파라미터로_파싱한다() {
        RequestLine requestLine = RequestLine.from("GET /login?account=gugu HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getQueryParameters().get("account")).isEqualTo("gugu");
    }

    @Test
    void 형식이_잘못된_요청_라인은_예외가_발생한다() {
        assertThatThrownBy(() -> RequestLine.from("GET /index.html"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
