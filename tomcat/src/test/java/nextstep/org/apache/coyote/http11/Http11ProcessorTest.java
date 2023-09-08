package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.Constants;
import org.apache.coyote.http11.Http11Processor;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.apache.coyote.http11.Constants.*;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();
        System.out.println(output);
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 200 OK ");
            softly.assertThat(output).contains("Content-Type: text/html;charset=utf-8 ");
            softly.assertThat(output).contains("Content-Length: 12 ");
            softly.assertThat(output).contains("Hello world!");
        });

    }

    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join(CRLF,
                "GET /anyThing.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String output = socket.output();
        String expectBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 404 Not Found ");
            softly.assertThat(output).contains("Content-Type: text/html;charset=utf-8 ");
            softly.assertThat(output).contains("Content-Length: 2426 ");
            softly.assertThat(output).contains(expectBody);
        });
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join(CRLF,
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String output = socket.output();
        String expectBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 200 OK ");
            softly.assertThat(output).contains("Content-Type: text/html;charset=utf-8 ");
            softly.assertThat(output).contains("Content-Length: 5564 ");
            softly.assertThat(output).contains(expectBody);
        });
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join(CRLF,
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String output = socket.output();
        String expectBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 200 OK ");
            softly.assertThat(output).contains("Content-Type: text/css;charset=utf-8 ");
            softly.assertThat(output).contains("Content-Length: 211991 ");
            softly.assertThat(output).contains(expectBody);
        });
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest = String.join(CRLF,
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String output = socket.output();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 302 Found ");
            softly.assertThat(output).contains("Location: /index ");
        });
    }

    @Test
    void getLoginPage() throws IOException {
        // given
        final String httpRequest = String.join(CRLF,
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String output = socket.output();
        String expectBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(output).contains("HTTP/1.1 200 OK ");
            softly.assertThat(output).contains("Content-Type: text/html;charset=utf-8 ");
            softly.assertThat(output).contains("Content-Length: 3797 ");
            softly.assertThat(output).contains(expectBody);
        });
    }
}
