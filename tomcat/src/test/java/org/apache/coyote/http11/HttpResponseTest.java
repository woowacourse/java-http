package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void 본문을_응답한다() throws Exception {
        final var outputStream = new ByteArrayOutputStream();
        final var response = new HttpResponse(outputStream);

        response.send("text/plain", "hello".getBytes(StandardCharsets.UTF_8));

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 200 OK")
                .contains("Content-Type: text/plain")
                .contains("Content-Length: 5")
                .endsWith("hello");
    }

    @Test
    void 리다이렉트한다() throws Exception {
        final var outputStream = new ByteArrayOutputStream();
        final var response = new HttpResponse(outputStream);
        response.addCookie("JSESSIONID=session-id");

        response.sendRedirect("/index.html");

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 302 Found")
                .contains("Set-Cookie: JSESSIONID=session-id")
                .contains("Location: /index.html");
    }
}
