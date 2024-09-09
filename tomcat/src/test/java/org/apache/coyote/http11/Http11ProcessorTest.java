package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.catalina.session.HttpSession;
import org.apache.catalina.session.HttpSessionManger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

@DisplayName("Http11Processor")
class Http11ProcessorTest {

    @DisplayName("Http11Processor의 실행을 확인한다.")
    @Test
    void process() {
        // given
        StubSocket socket = new StubSocket();
        Http11Processor processor = new Http11Processor(socket);

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

    @DisplayName("index 페이지 조회를 조회한다.")
    @Test
    void getIndexPage() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

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

    @DisplayName("로그인 페이지를 조회한다.")
    @Test
    void getLoginPage() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        Path resource = Path.of(getClass().getClassLoader()
                .getResource("static/login.html")
                .getPath());

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 3797 \r\n" +
                "\r\n" +
                Files.readString(resource);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("이미 로그인 된 사용자는 로그인 페이지 조회 시 index 페이지로 리다이렉트된다.")
    @Test
    void getLoginPageWithAlreadyLogin()  {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=randomUUID ",
                "",
                ""
        );

        HttpSessionManger httpSessionManger = new HttpSessionManger();
        HttpSession httpSession = new HttpSession("randomUUID");
        httpSession.setAttribute("user", new User("tester", "password", "test@gmail.com"));
        httpSessionManger.add(httpSession);

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html " +
                "\r\n";

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인 성공 시 index 페이지로 리다이렉트 된다.")
    @Test
    void login()  {
        // given
        String body = "account=test&password=password";
        InMemoryUserRepository.save(new User("test", "password", "test@gmail.com"));
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body
        );

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        List<String> expected = List.of(
                "HTTP/1.1 302 Found \r\n",
                "Set-Cookie: JSESSIONID=",
                "Location: /index.html " + "\r\n"
        );

        assertThat(socket.output()).contains(expected);
    }

    @DisplayName("로그인 실패 시 401 페이지로 리다이렉트 된다.")
    @Test
    void failLogin()  {
        // given
        String body = "account=noAccount&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body
        );

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /401.html " +
                "\r\n";

        assertThat(socket.output()).contains(expected);
    }

    @DisplayName("회원가입 페이지를 조회한다.")
    @Test
    void getRegisterPage() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        Path resource = Path.of(getClass().getClassLoader()
                .getResource("static/register.html")
                .getPath());
        String body = Files.readString(resource);

        String expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + body.getBytes().length + " \r\n" +
                "\r\n" +
                body;

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입 성공 시 index 페이지로 리다이렉트한다.")
    @Test
    void register() {
        // given
        String body = "account=test&password=password";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes().length + " ",
                "Content-Type: application/x-www-form-urlencoded ",
                "",
                body
        );

        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        String expected = "HTTP/1.1 302 Found \r\n" +
                "Location: /index.html " +
                "\r\n";

        assertThat(socket.output()).contains(expected);
    }
}
