package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
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
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
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
                "HTTP/1.1 302 Found",
                "Location: /index.html",
                "Content-Length: 0",
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

    @Test
    void HTML_정적_파일을_응답한다() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);
        final byte[] expectedBody = readStaticResource("index.html");

        response.forward("/index.html");

        final String output = outputStream.toString(StandardCharsets.UTF_8);
        assertThat(output)
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: " + expectedBody.length + "\r\n")
                .endsWith(new String(expectedBody, StandardCharsets.UTF_8));
    }

    @Test
    void CSS_정적_파일을_응답한다() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        response.forward("/css/styles.css");

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("Content-Type: text/css;charset=utf-8\r\n");
    }

    @Test
    void 존재하지_않는_정적_파일은_404_페이지를_응답한다() throws IOException {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);
        final byte[] expectedBody = readStaticResource("404.html");

        response.forward("/missing.html");

        final String output = outputStream.toString(StandardCharsets.UTF_8);
        assertThat(output)
                .startsWith("HTTP/1.1 404 Not Found\r\n")
                .contains("Content-Type: text/html;charset=utf-8\r\n")
                .contains("Content-Length: " + expectedBody.length + "\r\n")
                .endsWith(new String(expectedBody, StandardCharsets.UTF_8));
    }

    private byte[] readStaticResource(final String path) throws IOException {
        final URL resource = getClass().getClassLoader().getResource("static/" + path);
        return Files.readAllBytes(new File(resource.getFile()).toPath());
    }
}
