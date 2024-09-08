package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import com.techcourse.session.SessionManager;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.startline.HttpStatus;
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
        String expectedBody = "Hello world!";
        String expectedHttpStatus = HttpStatus.OK.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatus)
                .contains(expectedContentType);
    }

    @Test
    void index() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive"
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatus = HttpStatus.OK.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatus)
                .contains(expectedContentType);
    }

    @DisplayName("css 지원 테스트")
    @Test
    void css() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/css,*/*;q=0.1",
                "Connection: keep-alive"
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatus = HttpStatus.OK.getValue();
        String expectedContentType = "text/css";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatus)
                .contains(expectedContentType);
    }

    @DisplayName("로그인 페이지 접근 테스트")
    @Test
    void loginHtml() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatus = HttpStatus.OK.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatus)
                .contains(expectedContentType);
    }

    @DisplayName("로그인한 상태로 로그인 페이지에 접근하면, index 페이지로 redirect된다.")
    @Test
    void redirectWhenAlreadyLoggedIn() throws IOException {
        // given
        User user = new User("account", "password", "mail@mail.com");
        String jSessionId = SessionManager.addUser(user);
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive",
                "Cookie: JSESSIONID=" + jSessionId
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatus = HttpStatus.FOUND.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatus)
                .contains(expectedContentType);
    }

    @DisplayName("로그인 성공 테스트")
    @Test
    void loginSuccess() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=password HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatus = HttpStatus.FOUND.getValue();
        String expectedContentType = "text/html";
        String expectedHeader = "Set-Cookie: ";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatus)
                .contains(expectedContentType)
                .contains(expectedHeader);
    }

    @DisplayName("로그인 실패 테스트")
    @Test
    void loginFail() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login?account=gugu&password=notpassword HTTP/1.1",
                "Host: localhost:8080",
                "Accept: text/html",
                "Connection: keep-alive"
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/401.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatus = HttpStatus.UNAUTHORIZED.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatus)
                .contains(expectedContentType);
    }

    @DisplayName("회원가입 테스트")
    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 80",
                "Connection: keep-alive",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password&email=hkkang%40woowahan.com"
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expectedHttpStatus = HttpStatus.FOUND.getValue();
        String expectedContentType = "text/html";

        assertThat(socket.output())
                .contains(expectedBody)
                .contains(expectedHttpStatus)
                .contains(expectedContentType);
    }
}
