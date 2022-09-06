package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import nextstep.jwp.controller.LoginController;
import nextstep.jwp.db.InMemoryUserRepository;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.slf4j.LoggerFactory;
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
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html",
                "Hello world!"
        );
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
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html",
                responseBody
        );
    }

    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/css",
                responseBody
        );
    }

    @Test
    void notFound() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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
        assertThat(socket.output()).contains("HTTP/1.1 404 Not Found");
    }

    @Test
    void getLoginForm() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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

        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html",
                responseBody);
    }

    @Test
    void loginByValidAccount() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found \r\n",
                "Location: /index.html",
                "Set-Cookie: ");
    }

    @ParameterizedTest
    @CsvSource({"invalidAccount,password", "gugu,invalidPassword"})
    void loginByInvalidAccount(String account, String password) {
        // given
        final String httpRequest = String.join("\r\n",
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
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found",
                "Location: /401.html"
        );
    }

    @Test
    void getRegisterForm() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
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
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html",
                responseBody
        );
    }

    @Test
    void postRegister() {
        // given
        final String httpRequest = String.join("\r\n",
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
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found",
                "Location: /index.html"
        );
        assertThat(InMemoryUserRepository.findByAccount("wilgur513")).isPresent();
    }

    @Test
    void redirectToIndexPageUsingSession() {
        // given
        final ListAppender<ILoggingEvent> appender = new ListAppender<>();
        final Logger logger = (Logger) LoggerFactory.getLogger(LoginController.class);
        logger.addAppender(appender);
        appender.start();

        final String cookies = login();
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "Cookie: " + cookies,
                "",
                "account=wilgur513&password=password&email=wilgur513@woowahan.com");
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).contains(
                "HTTP/1.1 302 Found",
                "Location: /index.html"
        );
        assertThat(appender.list.get(0).getFormattedMessage()).isEqualTo(
                "Login User: User{id=1, account='gugu', email='hkkang@woowahan.com', password='password'}");
    }

    private String login() {
        final String httpRequest = String.join("\r\n",
                "POST /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        final String output = socket.output();

        for (String line : output.split("\n")) {
            if (line.startsWith("Set-Cookie:")) {
                return line.substring("Set-Cookie:".length()).trim();
            }
        }

        return null;
    }
}
