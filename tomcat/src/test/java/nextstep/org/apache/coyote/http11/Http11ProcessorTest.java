package nextstep.org.apache.coyote.http11;

import static nextstep.jwp.db.InMemoryUserRepository.findByAccount;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;
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
        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html; charset=utf-8 ",
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
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: " + new File(resource.getPath()).length() + " \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void loginPage() throws IOException {
        //given
        final String HttpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(HttpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        //when
        processor.process(socket);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: " + new File(resource.getPath()).length() + " \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void 로그인_성공() {
        //given
        String body = "account=gugu&password=password";
        final var request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        //when
        processor.process(socket);

        //then
        var expected = "HTTP/1.1 302 Found";

        assertAll(
                () -> assertThat(socket.output()).contains(expected),
                () -> assertThat(socket.output()).contains("Set-Cookie: JSESSIONID="),
                () -> assertThat(socket.output()).contains("Location: ")
        );
    }

    @Test
    void 로그인_실패() {
        //given
        String body = "account=123&password=123";
        final var request = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        //when
        processor.process(socket);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        var expected = "HTTP/1.1 401 Unauthorized \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: " + new File(resource.getPath()).length();

        assertThat(socket.output()).contains(expected);
    }

    @Test
    void registerPage() throws IOException {
        //given
        final String HttpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(HttpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        //when
        processor.process(socket);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html; charset=utf-8 \r\n" +
                "Content-Length: " + new File(resource.getPath()).length() + " \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    void register() {
        //given
        String body = "account=newnew&password=1234";
        final var request = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: " + body.length(),
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body);

        final var socket = new StubSocket(request);
        final Http11Processor processor = new Http11Processor(socket);

        //when
        processor.process(socket);
        //then
        var expected = "HTTP/1.1 302 Found";

        assertAll(
                () -> assertThat(socket.output()).contains(expected),
                () -> assertThat(socket.output()).contains("Location: "),
                () -> assertThatCode(() -> findByAccount("newnew").get()).doesNotThrowAnyException(),
                () -> assertThat(findByAccount("newnew").get().getPassword()).isEqualTo("1234")
        );
    }
}
