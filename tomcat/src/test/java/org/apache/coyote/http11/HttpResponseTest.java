package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void 전달받은_응답에_본문을_설정하면_상태와_바이트_길이를_출력한다() throws IOException {
        // given
        HttpResponse response = new HttpResponse();
        byte[] body = "한글".getBytes(StandardCharsets.UTF_8);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.setContent(HttpStatus.OK, body, "text/plain;charset=utf-8");
        response.writeTo(outputStream);

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Type: text/plain;charset=utf-8 \r\n")
                .contains("Content-Length: 6 \r\n")
                .endsWith("\r\n\r\n한글");
    }

    @Test
    void 전달받은_응답에서_리다이렉트하면_본문을_비우고_Location을_출력한다() throws IOException {
        // given
        HttpResponse response = new HttpResponse();
        response.setContent(HttpStatus.OK, "before".getBytes(StandardCharsets.UTF_8), "text/plain");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.sendRedirect("/index.html");
        response.writeTo(outputStream);

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n")
                .contains("Content-Length: 0 \r\n")
                .doesNotContain("Content-Type")
                .endsWith("\r\n\r\n");
    }

    @Test
    void OK_응답에_상태줄과_필수_헤더와_body를_출력한다() throws IOException {
        // given
        HttpResponse response = HttpResponse.ok("Hello world!", "text/html;charset=utf-8");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(outputStream.toString()).isEqualTo(expected);
    }

    @Test
    void 응답_Content_Length는_UTF_8_body의_바이트_크기다() throws IOException {
        // given
        HttpResponse response = HttpResponse.ok("한글", "text/plain;charset=utf-8");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        assertThat(outputStream.toString()).contains("Content-Length: 6 ");
    }

    @Test
    void redirect_응답에_302_상태와_Location_헤더를_출력한다() throws IOException {
        // given
        HttpResponse response = HttpResponse.redirect("/index.html");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        assertThat(outputStream.toString())
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n");
    }

    @Test
    void body가_없는_redirect_응답의_Content_Length는_0이다() throws IOException {
        // given
        HttpResponse response = HttpResponse.redirect("/index.html");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        assertThat(outputStream.toString())
                .contains("Content-Length: 0 \r\n")
                .endsWith("\r\n\r\n");
    }
}
