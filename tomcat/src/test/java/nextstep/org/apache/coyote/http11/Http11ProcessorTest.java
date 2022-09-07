package nextstep.org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.DisplayName;
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
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 성공시 302 응답과 함께 /index.html 로 리다이렉팅 한다.")
    @Test
    void login() {
        //given
        final String account = "gugu";
        final String password = "password";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=" + account + "&password=" + password);

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        //then
        final String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n";
        assertThat(socket.output()).contains(expected);
    }

    @DisplayName("로그인 실패시 302 응답과 함께 /401.html 로 리다이렉팅 한다.")
    @Test
    void login_Failed() {
        //given
        final String account = "gugu";
        final String password = "pp";
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=" + account + "&password=" + password);
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        //when
        processor.process(socket);

        //then
        final String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /401.html \r\n";
        assertThat(socket.output()).contains(expected);
    }

    @DisplayName("회원가입 성공시 회원 정보를 저장하고 302 응답과 함께 /index.html 로 리다이렉팅 한다.")
    @Test
    void register() {
        //given
        final String account = "east";
        final String password = "password";
        final String email = "xldk78@gmail.com";
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 58",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=" + account + "&password=" + password + "&email=" + email);
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        //when
        processor.process(socket);

        //then
        final String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html \r\n";
        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        assertAll(
                () -> assertThat(socket.output()).contains(expected),
                () -> assertThat(user.get()).isNotNull()
        );
    }
}
