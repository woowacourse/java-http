package nextstep.org.apache.coyote.http11;

import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.DisplayName;
import support.StubSocket;
import org.apache.coyote.http11.Http11Processor;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @DisplayName("요청 url이 /인 경우 Hello World를 반환한다.")
    @Test
    void home() {
        // given
        final String httpRequest= String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

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

    @DisplayName("요청 url이 /index.html인 경우 index.html view를 반환한다.")
    @Test
    void index() throws IOException {
        // given
        final String httpRequest= String.join("\r\n",
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
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("요청 url의 확장자가 css인 경우 알맞는 view를 찾아 반환한다.")
    @Test
    void css() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        var expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/css;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("회원가입을 완료하면 index.html 페이지로 리다이렉트한다.")
    @Test
    void userRegisterIsSuccess() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/html,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "",
                "account=green&email=green@0wooteco.com&password=1234");

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/index.html");
        var expected = "HTTP/1.1 302 Moved Permanently \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인이 성공하면 302를 반환하고 /index.html 리다이렉트한다.")
    @Test
    void userLoginIsSuccess() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "POST /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/index.html");

        //쿠키 포함 검증
        final String output = socket.output();
        String cookie = output.split("\r\n")[1];
        assertThat(cookie.contains("Set-Cookie: JSESSIONID=")).isTrue();

        String expected = "HTTP/1.1 302 Moved Permanently \r\n" +
                cookie + "\r\n"+
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("로그인이 성공하면 session이 정상적으로 생성되고, sessionId가 session에 저장되어있는지 검증한다.")
    @Test
    void userLoginValidateSession() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "POST /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final String output = socket.output();
        final String cookie = output.split("\r\n")[1];
        final String sessionId = cookie.split("JSESSIONID=")[1];

        final Session session = SessionManager.findSession(sessionId);
        assertThat(session.hasAttribute("user")).isTrue();
        assertThat(session.getId()).isEqualTo(sessionId);
    }

    @DisplayName("JSESSIONID가 request header에 있는 경우, 로그인을 하지 않아도 index.html로 리다이렉트")
    @Test
    void userLoginWithJSESSIONID() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "POST /login?account=gugu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive ",
                "Cookie: yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46"
        );

        final Session session = SessionManager.add("656cef62-e3c4-40bc-a8df-94732920ed46");
        session.addAttribute("user", new User("gugu", "password", "hkkang@woowahan.com"));

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/index.html");

        String expected = "HTTP/1.1 302 Moved Permanently \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 유저일 경우 401 페이지가 보여진다.")
    @Test
    void notExistUserException() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "POST /login?account=gu&password=password HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/401.html");
        final String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        var expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);

        assertThat(socket.output()).isEqualTo(expected);
    }

    @DisplayName("유저의 비밀번호가 틀릴 경우 401 페이지가 보여진다.")
    @Test
    void UserPasswordIsWrongException() throws IOException {
        //given
        final String httpRequest = String.join("\r\n",
                "POST /login?account=gugu&password=password1 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Accept: text/css,*/*;q=0.1 ",
                "Connection: keep-alive "
        );

        final var socket = new StubSocket(httpRequest);
        final Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass()
                .getClassLoader()
                .getResource("static/401.html");
        final String content = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        var expected = String.join("\r\n",
                "HTTP/1.1 401 Unauthorized ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);

        assertThat(socket.output()).isEqualTo(expected);
    }
}
