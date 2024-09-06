package org.apache.coyote.http11.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class LoginHandlerTest {

    @Test
    @DisplayName("GET '/login' 요청에 대한 응답이 정상적으로 처리된다.")
    void login() throws IOException {
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
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 3797 ",
                "",
                new String(Files.readAllBytes(new File(resource.getFile()).toPath())
                ));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("GET '/login' 요청 시 로그인 상태이면 '/index.html'로 리다이렉트한다.")
    void login_already_logged_in() {
        // given
        Session session = saveSession();

        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=" + session.getId(),
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /index.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST '/login' 요청에 대한 응답이 정상적으로 처리된다.")
    void login_post() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=gugu&password=password"
        );
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertAll(
                () -> assertThat(socket.output()).contains("HTTP/1.1 302 Found "),
                () -> assertThat(socket.output()).contains("Location: /index.html "),
                () -> assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=")
        );
    }

    @Test
    @DisplayName("POST '/login' 요청 시 계정이 존재하지 않으면 '/401.html'로 리다이렉트한다.")
    void login_post_not_found() {
        // given
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive",
                "Content-Length: 30",
                "",
                "account=notfound&password=password"
        );
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: /401.html ",
                "Content-Length: 0 ",
                "",
                "");

        assertThat(socket.output()).isEqualTo(expected);
    }

    private static Session saveSession() {
        User user = new User("account", "password", "example@gmail.com");
        InMemoryUserRepository.save(user);

        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.add(session);

        return session;
    }
}
