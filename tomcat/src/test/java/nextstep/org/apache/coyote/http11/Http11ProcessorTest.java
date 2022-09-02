package nextstep.org.apache.coyote.http11;

import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.StubSocket;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void process() {
        // given
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        HttpResponse response = HttpResponse.ok()
                .header("Content-Type", "text/html")
                .textBody("Hello world!")
                .build();

        assertThat(socket.output()).isEqualTo(response.writeValueAsString());
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final HttpResponse response = HttpResponse.ok()
                .header("Content-Type", "text/html")
                .textBody(responseBody)
                .build();

        assertThat(socket.output()).isEqualTo(response.writeValueAsString());
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final HttpResponse response = HttpResponse.ok()
                .header("Content-Type", "text/css")
                .textBody(responseBody)
                .build();

        assertThat(socket.output()).contains(response.writeValueAsString());
    }

    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /notFound.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final HttpResponse response = HttpResponse.notFound().build();
        assertThat(socket.output()).contains(response.writeValueAsString());
    }

    @Test
    void login() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        final HttpResponse response = HttpResponse.ok()
                .header("Content-Type", "text/html")
                .textBody(responseBody)
                .build();

        assertThat(socket.output()).contains(response.writeValueAsString());
    }

    @Test
    void loginByValidAccount() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        HttpResponse response = HttpResponse.redirect("/index.html").build();
        response.addHeader("Location", "/index.html");
        assertThat(socket.output()).contains(response.writeValueAsString());
    }

    @ParameterizedTest
    @CsvSource({"invalidAccount,password", "gugu,invalidPassword"})
    void loginByInvalidAccount(String account, String password) throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /login?account=" + account + "&password=" + password + " HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        HttpResponse response = HttpResponse.redirect("/401.html").build();
        response.addHeader("Location", "/401.html");
        assertThat(socket.output()).contains(response.writeValueAsString());
    }
}
