package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PostHttp11MethodHandlerTest {

    private final PostHttp11MethodHandler handler = new PostHttp11MethodHandler();

    @Test
    void 비밀번호가_틀리면_401_페이지로_리다이렉션한다() {
        // given
        String headers = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        // when
        String result = handler.handle(headers, "account=gugu&password=wrong");

        // then
        assertThat(result).contains("HTTP/1.1 302 Found");
        assertThat(result).contains("Location: 401.html");
    }

    @Test
    void 로그인이_정상적으로_처리되면_index_페이지로_리다이렉션한다() {
        // given
        String headers = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        // when
        String result = handler.handle(headers, "account=gugu&password=password");

        // then
        assertThat(result).contains("HTTP/1.1 302 Found");
        assertThat(result).contains("Location: index.html");
    }
}
