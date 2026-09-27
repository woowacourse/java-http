package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void sendOk() throws IOException {
        //given
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(output);

        //when
        response.send("text/html;charset=utf-8", "안녕");

        //then
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html;charset=utf-8\r\n"
                + "Content-Length: 6\r\n"
                + "\r\n"
                + "안녕"
        );
    }

    @Test
    void sendRedirect() throws IOException {
        //given
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(output);

        //when
        response.sendRedirect("/index.html");

        //then
        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 302 Found\r\n"
                + "Location: /index.html\r\n"
                + "Content-Length: 0\r\n"
                + "\r\n"
        );
    }
}
