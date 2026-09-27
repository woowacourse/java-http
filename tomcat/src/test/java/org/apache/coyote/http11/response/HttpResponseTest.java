package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 상태_라인_헤더_빈_줄_바디_순서로_응답을_만든다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.setBody("Hello world!", "text/html");

        // then
        String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 12 ",
            "",
            "Hello world!");
        assertThat(toString(response)).isEqualTo(expected);
    }

    @Test
    void Content_Length는_문자_수가_아닌_바이트_수로_계산한다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.setBody("안녕", "text/html");

        // then
        assertThat(toString(response)).contains("Content-Length: 6 ");
    }

    @Test
    void 리다이렉트_응답은_302_상태와_Location_헤더를_가진다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.sendRedirect("/index.html");
        response.addCookie("JSESSIONID", "abc");

        // then
        String expected = String.join("\r\n",
            "HTTP/1.1 302 Found ",
            "Location: /index.html ",
            "Set-Cookie: JSESSIONID=abc; Path=/; HttpOnly ",
            "Content-Length: 0 ",
            "",
            "");
        assertThat(toString(response)).isEqualTo(expected);
    }

    @Test
    void 상태만_지정하면_바디_없이_응답한다() {
        // given
        HttpResponse response = new HttpResponse();

        // when
        response.setStatus(HttpStatus.NOT_FOUND);

        // then
        assertThat(toString(response)).isEqualTo("HTTP/1.1 404 Not Found \r\nContent-Length: 0 \r\n\r\n");
    }

    private String toString(HttpResponse response) {
        return new String(response.getBytes(), StandardCharsets.UTF_8);
    }
}
