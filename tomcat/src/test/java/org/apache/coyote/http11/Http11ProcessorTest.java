package org.apache.coyote.http11;

import com.techcourse.config.ControllerConfig;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final var processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " \r\n" +
                "\r\n" +
                responseBody;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void CSS_정적_파일을_응답한다() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final String responseBody = readStaticResource("css/styles.css");

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK")
                .contains("Content-Type: text/css;charset=utf-8")
                .endsWith(responseBody);
    }

    @Test
    void JavaScript_정적_파일을_올바른_Content_Type으로_응답한다() throws IOException, URISyntaxException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final String responseBody = readStaticResource("js/scripts.js");

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK")
                .contains("Content-Type: application/javascript;charset=utf-8")
                .endsWith(responseBody);
    }

    @Test
    void 존재하지_않는_정적_파일은_404로_응답한다() {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/not-found.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 404 Not Found");
    }

    @Test
    void login() throws IOException, URISyntaxException {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/index.html");

        final byte[] expectedBody = Files.readAllBytes(
                Path.of(resource.toURI())
        );

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found")
                .endsWith(new String(expectedBody, StandardCharsets.UTF_8));
    }

    @Test
    void 로그인_페이지를_응답한다() throws Exception {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/login.html");

        final byte[] expectedBody = Files.readAllBytes(
                Path.of(resource.toURI())
        );

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK")
                .endsWith(new String(expectedBody, StandardCharsets.UTF_8));
    }

    @Test
    void 올바른_계정과_비밀번호로_로그인한다() {
        // given
        final String requestBody = "account=gugu&password=password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found");
    }

    @Test
    void 비밀번호가_틀리면_401_페이지로_리다이렉트한다() {
        // given
        final String requestBody = "account=gugu&password=wrong";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + requestBody.getBytes(StandardCharsets.UTF_8).length,
                "",
                requestBody);

        final var socket = new StubSocket(httpRequest);
        final RequestMapping requestMapping = new ControllerConfig().requestMapping();
        final Http11Processor processor = new Http11Processor(socket, requestMapping);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 303 See Other");
    }

    private String readStaticResource(final String resourcePath) throws IOException, URISyntaxException {
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/" + resourcePath);

        return Files.readString(Path.of(resource.toURI()));
    }
}
