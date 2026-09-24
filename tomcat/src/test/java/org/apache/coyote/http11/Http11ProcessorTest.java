package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import jakarta.servlet.http.HttpSession;
import java.net.URISyntaxException;
import org.apache.coyote.http11.Http11Processor.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void 로그인_페이지를_GET으로_조회한다() throws URISyntaxException {
        var socket = new StubSocket("GET /login HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK \r\n");
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void POST_로그인에_성공하면_index로_리다이렉트한다() throws URISyntaxException {
        var socket = post("/login", "account=gugu&password=password");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found \r\n");
        assertThat(socket.output()).contains("Location: /index.html");
        assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");

        HttpSession session = SessionManager.getInstance().findSession(findSessionId(socket));
        assertThat(session.getAttribute("user"))
                .isEqualTo(InMemoryUserRepository.findByAccount("gugu").orElseThrow());
    }

    @Test
    void 로그인한_쿠키로_로그인_페이지에_접근하면_index로_리다이렉트한다() throws URISyntaxException {
        var loginSocket = post("/login", "account=gugu&password=password");
        new Http11Processor(loginSocket).process(loginSocket);

        var socket = new StubSocket("GET /login HTTP/1.1\r\n"
                + "Cookie: JSESSIONID=" + findSessionId(loginSocket) + "\r\n\r\n");
        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found \r\n");
        assertThat(socket.output()).contains("Location: /index.html");
        assertThat(socket.output()).doesNotContain("Set-Cookie:");
    }

    @Test
    void 세션을_무효화하면_다시_로그인_페이지를_보여준다() throws URISyntaxException {
        var loginSocket = post("/login", "account=gugu&password=password");
        new Http11Processor(loginSocket).process(loginSocket);
        String sessionId = findSessionId(loginSocket);
        SessionManager.getInstance().findSession(sessionId).invalidate();

        var socket = new StubSocket("GET /login HTTP/1.1\r\n"
                + "Cookie: JSESSIONID=" + sessionId + "\r\n\r\n");
        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK \r\n");
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void 저장되지_않은_세션의_쿠키는_로그인_페이지를_보여준다() throws URISyntaxException {
        var socket = new StubSocket("GET /login HTTP/1.1\r\n"
                + "Cookie: JSESSIONID=unknown-id\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK \r\n");
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void POST_로그인에_실패하면_401페이지로_리다이렉트한다() throws URISyntaxException {
        var socket = post("/login", "account=gugu&password=wrong");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found \r\n");
        assertThat(socket.output()).contains("Location: /401.html");
        assertThat(SessionManager.getInstance().findSession(findSessionId(socket))).isNull();
    }

    @Test
    void 회원가입_페이지를_GET으로_조회한다() throws URISyntaxException {
        var socket = new StubSocket("GET /register HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK \r\n");
        assertThat(socket.output()).contains("<title>회원가입</title>");
    }

    @Test
    void POST로_회원가입하면_저장하고_index로_리다이렉트한다() throws URISyntaxException {
        var socket = post("/register",
                "account=new-user&password=password&email=new%40example.com");

        new Http11Processor(socket).process(socket);

        assertThat(InMemoryUserRepository.findByAccount("new-user")).isPresent();
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found \r\n");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    private String findSessionId(StubSocket socket) {
        return socket.output().split("Set-Cookie: JSESSIONID=", 2)[1].split("\r\n", 2)[0];
    }

    private StubSocket post(String uri, String body) {
        return new StubSocket(String.join("\r\n",
                "POST " + uri + " HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.length(),
                "",
                body));
    }

    @Test
    void process() throws URISyntaxException {
        // given
        final var socket = new StubSocket(
                "GET / HTTP/1.1\r\n"
                        + "Host: localhost:8080\r\n"
                        + "Cookie: JSESSIONID=existing-id\r\n"
                        + "\r\n");
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
    void index() throws IOException, URISyntaxException {
        // given
        final String httpRequest= String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=existing-id",
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
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
