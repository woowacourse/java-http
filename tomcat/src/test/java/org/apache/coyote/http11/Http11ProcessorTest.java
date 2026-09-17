package org.apache.coyote.http11;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    @DisplayName("/ 요청을 index.html 정적 리소스로 응답한다")
    void processTest() throws IOException {
        // when
        String response = process("/");

        // then
        assertThat(response).isEqualTo(expectedResponse("/index.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("index.html 정적 리소스를 응답한다")
    void index() throws IOException {
        // when
        String response = process("/index.html");

        // then
        assertThat(response).isEqualTo(expectedResponse("/index.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("확장자가 없는 HTML 경로는 .html을 붙여 응답한다")
    void extensionlessHtmlPath() throws IOException {
        // when
        String response = process("/login");

        // then
        assertThat(response).isEqualTo(expectedResponse("/login.html", "text/html;charset=utf-8"));
    }

    @Test
    @DisplayName("html은 text_html로 응답한다")
    void htmlTest() {
        // when
        String response = process("/index.html");

        // then
        assertThat(response)
                .contains("Content-Type: text/html;charset=utf-8 ");
    }

    @Test
    @DisplayName("css text_css로 응답한다")
    void css() throws IOException {
        // when
        String response = process("/css/styles.css");

        // then
        assertThat(response).isEqualTo(expectedResponse("/css/styles.css", "text/css;charset=utf-8"));
    }

    @Test
    @DisplayName("쿼리 스트링이 포함된 요청에서 경로에 해당하는 HTML을 응답한다")
    void queryString() throws IOException {
        // when
        String response = process("/login.html?account=gugu&password=password");

        // then
        assertThat(response).isEqualTo(expectedResponse("/login.html", "text/html;charset=utf-8"));
    }

    private String process(String path) {
        final var socket = new StubSocket(httpRequest(path));
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        return socket.output();
    }

    private String httpRequest(String path) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
    }

    private String expectedResponse(String path, String contentType) throws IOException {
        byte[] body = readResource(path);

        return "HTTP/1.1 200 OK \r\n"
                + "Content-Type: " + contentType + " \r\n"
                + "Content-Length: " + body.length + " \r\n"
                + "\r\n"
                + new String(body, StandardCharsets.UTF_8);
    }

    private byte[] readResource(String path) throws IOException {
        URL resource = getClass()
                .getClassLoader()
                .getResource("static" + path);

        return Files.readAllBytes(new File(Objects.requireNonNull(resource).getFile()).toPath());
    }

}
