package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RequestLineTest {

    @Test
    void 요청_항목이_부족하면_예외가_발생한다() {
        assertThatThrownBy(() -> new RequestLine("GET /login"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 요청_항목이_많으면_예외가_발생한다() {
        assertThatThrownBy(() -> new RequestLine("GET /login HTTP/1.1 extra"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void URI가_비어_있으면_예외가_발생한다() {
        assertThatThrownBy(() -> new RequestLine("GET  HTTP/1.1"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 요청_첫_줄에서_메서드_URI_HTTP버전을_읽는다() {
        RequestLine requestLine = new RequestLine("POST /login HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo("POST");
        assertThat(requestLine.getUri()).isEqualTo("/login");
        assertThat(requestLine.getHttpVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void 경로와_쿼리_문자열을_분리한다() {
        RequestLine requestLine = new RequestLine("GET /login?account=gugu&password= HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getQueryString()).isEqualTo("account=gugu&password=");
    }

    @Test
    void 쿼리가_없으면_빈_문자열을_반환한다() {
        RequestLine requestLine = new RequestLine("GET / HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/");
        assertThat(requestLine.getQueryString()).isEmpty();
    }

    @Test
    void 물음표_뒤에_값이_없으면_빈_쿼리_문자열을_반환한다() {
        RequestLine requestLine = new RequestLine("GET /login? HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getQueryString()).isEmpty();
    }
}
