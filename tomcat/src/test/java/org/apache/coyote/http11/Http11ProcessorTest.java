package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
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

class Http11ProcessorTest {

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

        private final String account = "account";
        private final String password = "password";
        private final User user = new User(account, password, "aaa@gmail.com");

        @BeforeEach
        void saveUser() {
            InMemoryUserRepository.save(user);
        }

        @AfterEach
        void deleteUser() {
            InMemoryUserRepository.delete(user);
        }

        @DisplayName("로그인 성공 시 올바르게 응답을 보낸다.")
        @Test
        void loginSuccess() {
            String sessionId = "sessionId";
            Session session = new Session(sessionId);
            String body = String.format("account=%s&password=%s", account, password);
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
                    "Set-Cookie: JSESSIONID=" + sessionId
            );
        }

        @DisplayName("로그인 실패 시 올바르게 응답을 보낸다.")
        @Test
        void loginFail() {
            String sessionId = "sessionId";
            Session session = new Session(sessionId);
            String body = String.format("account=%s&password=%s", "invalid account", "password");
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
    }
}
