package org.apache.coyote.http11.response;

import org.junit.jupiter.api.Test;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void 아무것도_설정하지_않으면_200_OK와_빈_본문이다() {
        final HttpResponse response = new HttpResponse();

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 0 ",
                "",
                "");
        assertThat(toString(response)).isEqualTo(expected);
    }

    @Test
    void 본문을_담은_응답_메시지를_생성한다() {
        final HttpResponse response = new HttpResponse();

        response.setBody("text/html", "Hello world!");

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(toString(response)).isEqualTo(expected);
    }

    @Test
    void 설정한_상태_코드로_상태_라인을_생성한다() {
        final HttpResponse response = new HttpResponse();

        response.setStatus(HttpStatus.NOT_FOUND);

        assertThat(toString(response)).startsWith("HTTP/1.1 404 Not Found \r\n");
    }

    @Test
    void Content_Length는_바이트_길이로_계산한다() {
        final HttpResponse response = new HttpResponse();

        response.setBody("text/html", "한글");

        assertThat(toString(response)).contains("Content-Length: 6 \r\n");
    }

    @Test
    void 리다이렉트_응답은_302와_Location_헤더를_가진다() {
        final HttpResponse response = new HttpResponse();

        response.sendRedirect("/index.html");

        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");
        assertThat(toString(response)).isEqualTo(expected);
    }

    @Test
    void 리다이렉트하면_앞서_담은_본문을_비운다() {
        final HttpResponse response = new HttpResponse();
        response.setBody("text/html", "Hello world!");

        response.sendRedirect("/index.html");

        final String message = toString(response);
        assertThat(message).doesNotContain("Content-Type");
        assertThat(message).contains("Content-Length: 0 \r\n");
        assertThat(message).endsWith("\r\n\r\n");
    }

    @Test
    void 추가한_헤더가_응답_메시지에_포함된다() {
        final HttpResponse response = new HttpResponse();
        response.sendRedirect("/index.html");

        response.setHeader("Set-Cookie", "JSESSIONID=656cef62");

        assertThat(toString(response)).contains("Set-Cookie: JSESSIONID=656cef62 \r\n");
    }

    private String toString(final HttpResponse response) {
        return new String(response.getBytes(), UTF_8);
    }
}
