package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.controller.ControllerRegistry;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.session.UuidSessionGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class Http11ProcessorTest {

    @BeforeEach
    void setUp() {
        ControllerRegistry.registerControllers(RequestMapping.getInstance());
    }

    @Nested
    class ResourceFileTest {
        @DisplayName("특정 엔드포인트에 대한 올바른 HTML 파일을 반환한다.")
        @ParameterizedTest
        @ValueSource(strings = {"/index", "/login", "/register"})
        void getResourceHtml(String requestPath) throws IOException {
            String httpRequest = String.join("\r\n",
                    "GET " + requestPath + " HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new UuidSessionGenerator());

            processor.process(socket);

            String responseBody = getFile(requestPath + ".html");
            int contentLength = responseBody.getBytes().length;

            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/html;charset=utf-8 \r\n",
                    "Content-Length: " + contentLength + " \r\n",
                    responseBody
            );
        }

        @DisplayName("CSS 파일을 반환한다.")
        @Test
        void getCssFile() throws IOException {
            String httpRequest = String.join("\r\n",
                    "GET /css/styles.css HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "",
                    "");

            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new UuidSessionGenerator());

            processor.process(socket);

            String responseBody = getFile("/css/styles.css");
            int contentLength = responseBody.getBytes().length;

            assertThat(socket.output()).contains(
                    "HTTP/1.1 200 OK \r\n",
                    "Content-Type: text/css;charset=utf-8 \r\n",
                    "Content-Length: " + contentLength + " \r\n",
                    responseBody
            );
        }

        private String getFile(String fileName) throws IOException {
            URL resource = getClass().getClassLoader().getResource("static/" + fileName);
            return new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        }
    }

    @Nested
    class LoginTest {

        private final User user1 = new User("account", "password", "email");
        private final User user2 = new User("account2", "password2", "email2");
        private final Session session = new Session("sessionId");
        private final SessionManager sessionManager = new SessionManager();

        @BeforeEach
        void saveUser() {
            InMemoryUserRepository.save(user1);
            InMemoryUserRepository.save(user2);
        }

        @AfterEach
        void deleteUser() {
            InMemoryUserRepository.delete(user1);
            InMemoryUserRepository.delete(user2);
            session.invalidate();
            sessionManager.remove(session);
        }

        @DisplayName("로그인 성공 시 올바르게 응답을 보낸다.")
        @Test
        void loginSuccess() {
            String body = String.format("account=%s&password=%s", "account", "password");
            int contentLength = body.getBytes().length;
            String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + contentLength,
                    "",
                    body);

            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, () -> session);

            processor.process(socket);

            assertThat(socket.output()).contains(
                    "HTTP/1.1 302 Found \r\n",
                    "Location: /index.html \r\n",
                    "Set-Cookie: JSESSIONID=" + session.getId()
            );
        }

        @DisplayName("로그인 실패 시 올바르게 응답을 보낸다.")
        @Test
        void loginFail() {
            String body = String.format("account=%s&password=%s", "invalid", "password");
            int contentLength = body.getBytes().length;
            String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + contentLength,
                    "",
                    body);

            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, () -> session);

            processor.process(socket);

            assertThat(socket.output()).contains(
                    "HTTP/1.1 302 Found \r\n",
                    "Location: /401.html \r\n"
            );
        }

        @DisplayName("세션이 존재하는 클라이언트가 로그인 요청 시 올바르게 세션 데이터를 업데이트한다.")
        @Test
        void sessionUserLogin() {
            session.setAttribute("user", user1);
            sessionManager.add(session);
            String body = String.format("account=%s&password=%s", "account2", "password2");
            int contentLength = body.getBytes().length;
            String httpRequest = String.join("\r\n",
                    "POST /login HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + contentLength,
                    "Cookie: JSESSIONID=" + session.getId(),
                    "",
                    body);

            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, () -> session);

            processor.process(socket);

            assertAll(
                    () -> assertThat(socket.output()).contains(
                        "HTTP/1.1 302 Found \r\n",
                        "Location: /index.html \r\n"
                    ).doesNotContain("Set-Cookie: JSESSIONID=" + session.getId()),
                    () -> {
                        HttpSession session = sessionManager.findSession(this.session.getId());
                        User user = (User) session.getAttribute("user");
                        assertThat(user.getAccount()).isEqualTo(user2.getAccount());
                    }
            );
        }
    }

    @Nested
    class RegisterTest {

        @DisplayName("회원 가입 시 올바르게 응답을 보낸다.")
        @Test
        void register() {
            String account = "account";
            User user = new User(account, "password", "kkk@gmail.com");
            String body = "account=account&password=password&email=kkk@gmail.com";
            int contentLength = body.getBytes().length;
            String httpRequest = String.join("\r\n",
                    "POST /register HTTP/1.1 ",
                    "Host: localhost:8080 ",
                    "Connection: keep-alive ",
                    "Content-Length: " + contentLength,
                    "",
                    body);

            StubSocket socket = new StubSocket(httpRequest);
            Http11Processor processor = new Http11Processor(socket, new UuidSessionGenerator());

            processor.process(socket);

            assertAll(
                    () -> assertThat(socket.output()).contains(
                            "HTTP/1.1 302 Found \r\n",
                            "Location: /index.html \r\n"
                    ),
                    () -> assertThat(InMemoryUserRepository.findByAccount(account)).isNotEmpty()
            );
            InMemoryUserRepository.delete(user);
        }
    }
}
