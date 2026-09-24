package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {

    @Test
    void 요청_라인을_파싱한다() {
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        assertThat(requestLine.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(requestLine.getPath()).isEqualTo("/index.html");
        assertThat(requestLine.getVersion()).isEqualTo("HTTP/1.1");
    }

    @Test
    void 쿼리_스트링을_경로와_분리한다() {
        final RequestLine requestLine = RequestLine.from("GET /login?account=gugu HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/login");
        assertThat(requestLine.getQueryString()).isEqualTo("account=gugu");
    }

    @Test
    void 쿼리_스트링은_디코딩하지_않고_그대로_둔다() {
        final RequestLine requestLine = RequestLine.from("GET /login?account=a%26b HTTP/1.1");

        assertThat(requestLine.getQueryString()).isEqualTo("account=a%26b");
    }

    @Test
    void 쿼리_스트링이_없으면_빈_문자열이다() {
        final RequestLine requestLine = RequestLine.from("GET /index.html HTTP/1.1");

        assertThat(requestLine.getQueryString()).isEmpty();
    }

    @Test
    void 경로가_비어_있으면_루트로_본다() {
        final RequestLine requestLine = RequestLine.from("GET http://localhost:8080 HTTP/1.1");

        assertThat(requestLine.getPath()).isEqualTo("/");
    }

    @Test
    void 요청_라인_형식이_잘못되면_예외가_발생한다() {
        assertThatThrownBy(() -> RequestLine.from("GET /index.html"))
                .isInstanceOf(HttpRequestParseException.class);
    }

    @Test
    void 지원하지_않는_메서드면_예외가_발생한다() {
        assertThatThrownBy(() -> RequestLine.from("DELETE / HTTP/1.1"))
                .isInstanceOf(HttpRequestParseException.class);
    }

    @Test
    void URI_형식이_잘못되면_예외가_발생한다() {
        assertThatThrownBy(() -> RequestLine.from("GET /login?x=%zz HTTP/1.1"))
                .isInstanceOf(HttpRequestParseException.class);
    }

    @Test
    void 경로가_없는_URI면_예외가_발생한다() {
        assertThatThrownBy(() -> RequestLine.from("GET foo:bar HTTP/1.1"))
                .isInstanceOf(HttpRequestParseException.class);
    }
}
