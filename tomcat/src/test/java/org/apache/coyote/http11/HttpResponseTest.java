package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void 일반_응답을_전송한다() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);
        response.setStatus("200 OK");
        response.addHeader("Content-Type", "text/html;charset=utf-8");
        response.setBody("Hello world!".getBytes(StandardCharsets.UTF_8));

        response.send();

        final String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");
        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    @Test
    void 리다이렉트_응답을_전송한다() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        response.sendRedirect("/index.html");

        final String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");
        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(expected);
    }

    @Test
    void 응답에_쿠키를_추가한다() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);
        response.setStatus("200 OK");
        response.addCookie("JSESSIONID", "session-id");

        response.send();

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("Set-Cookie: JSESSIONID=session-id");
    }
}
