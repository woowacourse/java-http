package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void 요청_라인을_메서드와_요청_대상과_HTTP_버전으로_해석한다() {
        RequestLine requestLine = RequestLine.from("GET /login?account=gugu HTTP/1.1");

        assertThat(requestLine.method()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.target().path()).isEqualTo("/login");
        assertThat(requestLine.target().findQueryParameter("account"))
                .contains("gugu");
        assertThat(requestLine.version()).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    void 올바르지_않은_요청_라인은_허용하지_않는다() {
        assertThatThrownBy(() -> RequestLine.from("GET /login"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
