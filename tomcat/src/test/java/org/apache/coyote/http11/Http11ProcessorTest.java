package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.UUID;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    @DisplayName("Hello World!를 응답한다.")
    @Test
    void hello() {
        // given
        final StubSocket socket = new StubSocket();
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!"
        );

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("인덱스 페이지를 응답한다.")
    @Test
    void indexPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );

        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 페이지를 응답한다.")
    @Test
    void loginPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output())
                .contains(HttpStatus.OK.getStatusCode() + " " + HttpStatus.OK.getStatusMessage())
                .contains("Content-Type: " + ContentType.TEXT_HTML.getContentType())
                .contains(expectedBody);
    }

    @DisplayName("로그인에 성공하면 인덱스 페이지로 리다이렉트한다.")
    @Test
    void redirectToIndexPageWhenLoginSuccess() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "account=gugu&password=password"
        );
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains(HttpStatus.FOUND.getStatusCode() + " " + HttpStatus.FOUND.getStatusMessage())
                .contains("Content-Type: " + ContentType.TEXT_HTML.getContentType())
                .contains("Location: /index.html")
                .contains("Set-Cookie: JSESSIONID=");
    }

    @DisplayName("로그인한 후 로그인 페이지에 접근하면 인덱스 페이지로 리다이렉트한다.")
    @Test
    void redirectToIndexPageWhenAccessLoginPageAndAlreadyAuthenticated() {
        // given
        User user = new User("nyangin", "password", "nyangin@email.com");
        InMemoryUserRepository.save(user);

        UUID sessionId = UUID.randomUUID();
        Session session = new Session(sessionId.toString());
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);

        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + session.getId(),
                "",
                "");
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .contains(HttpStatus.FOUND.getStatusCode() + " " + HttpStatus.FOUND.getStatusMessage())
                .contains("Location: /index.html");
    }

    @DisplayName("로그인에 실패하면 401 페이지를 응답한다.")
    @Test
    void redirectTo401PageWhenLoginFail() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "account=nyangin&password=password"
        );
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        
        // when
        processor.process(socket);
    
        // then
        assertThat(socket.output())
                .contains(HttpStatus.FOUND.getStatusCode() + " " + HttpStatus.FOUND.getStatusMessage())
                .contains("Content-Type: " + ContentType.TEXT_HTML.getContentType())
                .contains("Location: /401.html");
    }
    
    @DisplayName("회원가입 페이지를 응답한다.")
    @Test
    void register() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        
        // when
        processor.process(socket);
    
        // then
        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        String expectedBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output())
                .contains(HttpStatus.OK.getStatusCode() + " " + HttpStatus.OK.getStatusMessage())
                .contains("Content-Type: " + ContentType.TEXT_HTML.getContentType())
                .contains(expectedBody);
    }
    
    @DisplayName("회원가입에 성공하면 인덱스 페이지로 리다이렉트한다.")
    @Test
    void redirectToIndexPageWhenRegisterSuccess() {
        // given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 59",
                "",
                "account=nyangin&password=password&email=nyangin%40email.com"
        );
        final StubSocket socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);
        
        // when
        processor.process(socket);
    
        // then
        assertThat(socket.output())
                .contains(HttpStatus.FOUND.getStatusCode() + " " + HttpStatus.FOUND.getStatusMessage())
                .contains("Content-Type: " + ContentType.TEXT_HTML.getContentType())
                .contains("Location: /index.html");
    }
}
