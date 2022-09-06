package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import support.StubSocket;

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
    void getLoginForm() throws IOException {
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
                "POST /login?account=gugu&password=password HTTP/1.1 ",
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
    void loginByInvalidAccount(String account, String password) {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /login?account=" + account + "&password=" + password + " HTTP/1.1 ",
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

    @Test
    void getRegisterForm() {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /register HTTP/1.1 ",
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
        HttpResponse response = HttpResponse.ok().fileBody("/register.html").build();
        response.addHeader("Content-Type", "text/html");
        assertThat(socket.output()).contains(response.writeValueAsString());
    }

    @Test
    void postRegister() {
        // given
        final String httpRequest= String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "Content-Length: 66",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=wilgur513&password=password&email=wilgur513@woowahan.com");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        HttpResponse response = HttpResponse.redirect("/index.html").build();
        assertThat(socket.output()).contains(response.writeValueAsString());
        assertThat(InMemoryUserRepository.findByAccount("wilgur513")).isPresent();
    }
}
