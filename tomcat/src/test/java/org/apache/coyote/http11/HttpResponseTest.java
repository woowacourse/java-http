package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void 정상_응답을_작성한다() throws Exception {

        // given
        final HttpResponse response = new HttpResponse();

        final byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);

        response.ok("text/html;charset=utf-8", body);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        final String result = outputStream.toString(StandardCharsets.UTF_8);

        assertThat(result).contains("HTTP/1.1 200 OK")
                .contains("Content-Type: text/html;charset=utf-8")
                .contains("Content-Length: " + body.length)
                .endsWith("Hello world!");
    }

    @Test
    void 리다이렉트_응답을_작성한다() throws Exception {

        // given
        final HttpResponse response = new HttpResponse();

        response.sendRedirect("/index.html");

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        final String result = outputStream.toString(StandardCharsets.UTF_8);

        assertThat(result)
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html")
                .contains("Content-Length: 0");
    }

    @Test
    void 추가_응답_헤더를_작성한다() throws Exception {

        // given
        final HttpResponse response = new HttpResponse();

        response.addHeader("Set-Cookie", "JSESSIONID=session-id");

        response.ok("text/html;charset=utf-8", new byte[0]);

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // when
        response.writeTo(outputStream);

        // then
        final String result = outputStream.toString(StandardCharsets.UTF_8);

        assertThat(result).contains("Set-Cookie: JSESSIONID=session-id");
    }
}