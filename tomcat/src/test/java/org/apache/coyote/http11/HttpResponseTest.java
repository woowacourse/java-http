package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 리다이렉트_응답은_본문_없이_위치와_길이를_포함한다() {
        // given
        final HttpResponse response = HttpResponse.init();
        response.addStatusLine(StatusLine.http11(HttpStatus.FOUND));

        // when
        response.sendRedirect("/index");

        // then
        assertThat(response.getMessage()).isEqualTo(String.join("\r\n",
            "HTTP/1.1 302 Found ",
            "Location: /index ",
            "Content-Length: 0 "));
    }

    @Test
    void 포워드_응답의_상태를_정적_리소스_처리가_덮어쓰지_않는다() throws Exception {
        // given
        final HttpResponse response = HttpResponse.init();
        response.addStatusLine(StatusLine.http11(HttpStatus.UNAUTHORIZED));

        // when
        new StaticResourceHandler().handle("/401.html", response);

        // then
        assertThat(response.getMessage()).startsWith("HTTP/1.1 401 Unauthorized ");
    }
}
