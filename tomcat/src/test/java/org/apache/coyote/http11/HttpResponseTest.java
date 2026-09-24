package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 리다이렉트_응답은_본문_없이_위치와_길이를_포함한다() {
        // given
        final HttpResponse response = HttpResponse.init();

        // when
        response.sendRedirect(HttpStatus.FOUND, "/index");

        // then
        assertThat(response.getMessage()).isEqualTo(String.join("\r\n",
            "HTTP/1.1 302 Found ",
            "Location: /index ",
            "Content-Length: 0 "));
    }

    @Test
    void 포워드_응답의_상태를_정적_리소스_처리가_덮어쓰지_않는다() throws Exception {
        // given
        final HttpRequest request = new HttpRequest(
            new RequestLine(HttpMethod.POST, "/login", HttpVersion.VERSION_11),
            HttpHeaders.empty(),
            "");
        final HttpResponse response = HttpResponse.init();
        response.forward(HttpStatus.UNAUTHORIZED, "/401.html");

        // when
        new StaticResourceHandler().handle(request, response);

        // then
        assertThat(response.getMessage()).startsWith("HTTP/1.1 401 Unauthorized ");
    }
}
