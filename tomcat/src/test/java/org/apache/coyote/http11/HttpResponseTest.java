package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void 상태_헤더_빈_줄_본문_순서로_응답한다() throws IOException {
        HttpResponse response = new HttpResponse();
        response.setHeader("Content-Type", "text/plain;charset=utf-8");
        response.setBody("Hello");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.write(output);

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/plain;charset=utf-8\r\n"
                        + "Content-Length: 5\r\n"
                        + "\r\n"
                        + "Hello");
    }

    @Test
    void 본문의_UTF8_바이트_길이를_ContentLength에_설정한다() throws IOException {
        HttpResponse response = new HttpResponse();
        response.setBody("한글");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.write(output);

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 200 OK\r\nContent-Length: 6\r\n\r\n한글");
    }

    @Test
    void 본문_없는_리다이렉트_응답을_생성한다() throws IOException {
        HttpResponse response = new HttpResponse();
        response.setStatus(HttpStatus.FOUND);
        response.setHeader("Location", "/index.html");
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.write(output);

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 302 Found\r\n"
                        + "Location: /index.html\r\n"
                        + "Content-Length: 0\r\n"
                        + "\r\n");
    }
}
