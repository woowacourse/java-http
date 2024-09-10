package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    @DisplayName("GET / 요청 시 index.html 파일을 반환한다")
    void process() throws URISyntaxException, IOException {
        //given
        final String httpRequest = resolveGetRequestByPath("/");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expected = resolveResponse("html", resource);
        assertThat(socket.output()).isEqualTo(expected);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET /index.html 요청 시 index.html 파일을 반환한다")
    @Test
    void index() throws IOException, URISyntaxException {
        // given
        final String httpRequest = resolveGetRequestByPath("/index.html");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expected = resolveResponse("html", resource);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET /css/styles.css 요청 시 style.css 파일을 반환한다")
    @Test
    void style() throws IOException, URISyntaxException {
        // given
        final String httpRequest = resolveGetRequestByPath("/css/styles.css");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String expected = resolveResponse("css", resource);
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("GET http://localhost:8080/login?account=gugu&password=password 요청 시 login.html 파일을 반환한다")
    @Test
    void login() throws IOException, URISyntaxException {
        // given
        final String httpRequest = resolveGetRequestByPath("http://localhost:8080/login?account=gugu&password=password");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String expected = resolveResponse("html", resource);
        assertThat(socket.output()).isEqualTo(expected);
    }

    private String resolveResponse(String extension, URL url) throws URISyntaxException, IOException {
        String file = Files.readString(Path.of(url.toURI()));
        return "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/" + extension + ";charset=utf-8 \r\n" +
                "Content-Length: " + file.getBytes().length + " \r\n" +
                "\r\n" +
                file;
    }

    private String resolveGetRequestByPath(String path) {
        return String.join("\r\n",
                "GET " + path + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
    }
}
