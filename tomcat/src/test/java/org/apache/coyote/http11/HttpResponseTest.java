package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

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
