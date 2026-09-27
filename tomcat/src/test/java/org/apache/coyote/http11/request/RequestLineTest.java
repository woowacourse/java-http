package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void 요청_라인에서_메서드와_경로를_파싱한다() {
        RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        assertThat(requestLine.hasMethod(HttpMethod.GET)).isTrue();
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
    }

    @Test
    void 쿼리스트링은_경로에서_분리한다() {
        RequestLine requestLine = RequestLine.from("GET /login?account=gugu HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/login");
    }

    @Test
    void 형식이_잘못된_요청_라인은_예외가_발생한다() {
        assertThatThrownBy(() -> RequestLine.from("GET /index.html"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
