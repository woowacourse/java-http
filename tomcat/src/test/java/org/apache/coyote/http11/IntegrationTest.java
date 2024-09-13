package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class IntegrationTest {

    @DisplayName("루트 페이지를 반환한다.")
    @Test
    void process() {
        final var socket = new StubSocket();
        final var processor = new Http11Processor(socket);

        processor.process(socket);

        assertThat(socket.output())
                .contains("HTTP/1.1")
                .contains("OK")
                .contains("Content-Type: text/html")
                .contains("Hello world!");
    }

    @DisplayName("리소스 지원 테스트")
    @Nested
    class GetResource {

        @DisplayName("html(index.html) 테스트")
        @Test
        void html() throws IOException {
            final String httpRequest = String.join("\r\n",
                    "GET /index.html HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            final URL resource = getClass().getClassLoader().getResource("static/index.html");
            String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output())
                    .contains("HTTP/1.1")
                    .contains("OK")
                    .contains("Content-Type: text/html")
                    .contains(body);
        }

        @DisplayName("css 테스트")
        @Test
        void css() throws IOException {
            final String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
            String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output())
                    .contains("HTTP/1.1")
                    .contains("OK")
                    .contains("Content-Type: text/css")
                    .contains(body);
        }

        @DisplayName("js 테스트")
        @Test
        void js() throws IOException {
            final String httpRequest = String.join("\r\n",
                    "GET /js/scripts.js HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            final URL resource = getClass().getClassLoader().getResource("static/js/scripts.js");
            String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output())
                    .contains("HTTP/1.1")
                    .contains("OK")
                    .contains("Content-Type: text/js")
                    .contains(body);
        }
    }

    @DisplayName("로그인 상황 테스트")
    @Nested
    class LoginTest {

        @DisplayName("get 요청시 로그인 페이지를 반환한다.")
        @Test
        void getLogin() throws IOException {
            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            final URL resource = getClass().getClassLoader().getResource("static/login.html");
            String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output())
                    .contains("HTTP/1.1")
                    .contains("OK")
                    .contains("Content-Type: text/html")
                    .contains(body);
        }

        @DisplayName("이미 로그인한 상태라면 get 요청시 index 페이지가 반환된다.")
        @Test
        void loginRedirectTest() {
            User user = new User("hogee", "hogee", "hogee@hogee.com");
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute(user.getAccount(), user);
            SessionManager.getInstance().add(session);

            final String httpRequest = String.join("\r\n",
                    "GET /login HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "Cookie: JSESSIONID=" + session.getId());

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            assertThat(socket.output())
                    .contains("HTTP/1.1")
                    .contains("Found")
                    .contains("Location: /index");
        }

        @DisplayName("post 요청시 가입한 사용자라면 로그인을 성공한다.")
        @Test
        void postLogin_success() {
            User user = new User("hogee", "hogee", "hogee@hogee.com");
            InMemoryUserRepository.save(user);
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute(user.getAccount(), user);
            SessionManager.getInstance().add(session);

            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1",
                    "Content-Length: 28",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "",
                    "account=hogee&password=hogee");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            assertThat(socket.output())
                    .contains("HTTP/1.1")
                    .contains("Found")
                    .contains("Location: /index")
                    .contains("Set-Cookie: JSESSIONID=");
        }

        @DisplayName("post 요청시 가입하지 않은 사용자라면 500 페이지를 반환한다.")
        @Test
        void postLogin_Fail() throws IOException {
            User user = new User("hogee", "hogee", "hogee@hogee.com");
            Session session = new Session(UUID.randomUUID().toString());
            session.setAttribute(user.getAccount(), user);
            SessionManager.getInstance().add(session);

            final String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1",
                    "Content-Length: 30",
                    "Host: localhost:8080",
                    "Connection: keep-alive",
                    "",
                    "account=hohoho&password=hohoho");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            final URL resource = getClass().getClassLoader().getResource("static/500.html");
            String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output())
                    .contains("HTTP/1.1")
                    .contains("Internal Server Error")
                    .contains("Content-Type: text/html")
                    .contains(body);
        }
    }

    @DisplayName("회원가입 테스트")
    @Nested
    class RegisterTest {
        @DisplayName("get 요청을 하면 회원가입 페이지가 반환된다.")
        @Test
        void getRegister() throws IOException {
            final String httpRequest = String.join("\r\n",
                    "GET /register HTTP/1.1",
                    "Host: localhost:8080",
                    "Connection: keep-alive");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            final URL resource = getClass().getClassLoader().getResource("static/register.html");
            String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            assertThat(socket.output())
                    .contains("HTTP/1.1")
                    .contains("OK")
                    .contains("Content-Type: text/html")
                    .contains(body);
        }

        @DisplayName("Post 요청을 하면 회원가입이 된다.")
        @Test
        void postRegister() {
            final String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1",
                    "Host: localhost:8080",
                    "Content-Length: 70",
                    "Connection: keep-alive",
                    "",
                    "account=hogee&password=hogee&email=hogee%40hogee");

            final var socket = new StubSocket(httpRequest);
            final Http11Processor processor = new Http11Processor(socket);

            processor.process(socket);

            assertThat(socket.output())
                    .contains("HTTP/1.1")
                    .contains("OK")
                    .contains("Set-Cookie: JSESSIONID=");
        }
    }

    @DisplayName("승인된 주소가 아니라면 404 페이지 반환")
    @Test
    void postRegister() throws IOException {
        final String httpRequest = String.join("\r\n",
                "GET /hogee HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        processor.process(socket);

        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        String body = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output())
                .contains("HTTP/1.1")
                .contains("Not Found")
                .contains(body);
    }
}
