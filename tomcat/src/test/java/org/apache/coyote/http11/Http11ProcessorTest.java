package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @Test
    void 기본경로_페이지_응답() throws IOException {
        // given
        StubSocket socket = new StubSocket();
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");

        String output = socket.output();
        String expectedStatusLine = "HTTP/1.1 200 OK";
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(output).contains(expectedStatusLine),
                () -> assertThat(output).contains(expectedPage)
        );
    }

    @Test
    void 로그인_페이지로_응답() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");

        String output = socket.output();
        String expectedStatusLine = "HTTP/1.1 200 OK";
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(output).contains(expectedStatusLine),
                () -> assertThat(output).contains(expectedPage)
        );
    }

    @Test
    void 회원가입_페이지로_이동() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");

        String output = socket.output();
        String expectedStatusLine = "HTTP/1.1 200 OK";
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(output).contains(expectedStatusLine),
                () -> assertThat(output).contains(expectedPage)
        );
    }

    @Test
    void HTML_자원_응답() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/index.html");

        String output = socket.output();
        String expectedStatusLine = "HTTP/1.1 200 OK";
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(output).contains(expectedStatusLine),
                () -> assertThat(output).contains(expectedPage)
        );
    }

    @Test
    void JS_자원_응답() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /js/scripts.js HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");

        String output = socket.output();
        String expectedStatusLine = "HTTP/1.1 200 OK";
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(output).contains(expectedStatusLine),
                () -> assertThat(output).contains(expectedPage)
        );
    }

    @Test
    void CSS_자원_응답() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");

        String output = socket.output();
        String expectedStatusLine = "HTTP/1.1 200 OK";
        String expectedPage = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertAll(
                () -> assertThat(output).contains(expectedStatusLine),
                () -> assertThat(output).contains(expectedPage)
        );
    }
}
