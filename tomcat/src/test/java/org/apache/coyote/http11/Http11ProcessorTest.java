package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import java.net.URISyntaxException;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void 파비콘_파일이_없으면_404로_응답한다() {
        var socket = new StubSocket("GET /favicon.ico HTTP/1.1\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 404 Not Found\r\n");
        assertThat(socket.output()).endsWith("Content-Length: 0\r\n\r\n");
    }

    @Test
    void 존재하지_않는_정적_파일은_404로_응답한다() {
        var socket = new StubSocket("GET /missing.html HTTP/1.1\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 404 Not Found\r\n");
        assertThat(socket.output()).endsWith("Content-Length: 0\r\n\r\n");
    }

    @Test
    void 로그인_페이지를_GET으로_조회한다() throws URISyntaxException {
        var socket = new StubSocket("GET /login HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void POST_로그인에_성공하면_index로_리다이렉트한다() throws URISyntaxException {
        var socket = post("/login", "account=gugu&password=password");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(socket.output()).contains("Location: /index.html");
        assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
        assertThat(socket.output()).containsOnlyOnce("Set-Cookie:");
        assertThat(socket.output()).endsWith("Content-Length: 0\r\n\r\n");

        Session session = SessionManager.getInstance().findSession(findSessionId(socket));
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

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
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

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void 저장되지_않은_세션의_쿠키는_로그인_페이지를_보여준다() throws URISyntaxException {
        var socket = new StubSocket("GET /login HTTP/1.1\r\n"
                + "Cookie: JSESSIONID=unknown-id\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void POST_로그인에_실패하면_401페이지로_리다이렉트한다() throws URISyntaxException {
        var socket = post("/login", "account=gugu&password=wrong");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(socket.output()).contains("Location: /401.html");
        assertThat(SessionManager.getInstance().findSession(findSessionId(socket))).isNull();
    }

    @Test
    void 빈_POST_로그인_요청은_로그인_페이지를_보여준다() throws URISyntaxException {
        var socket = post("/login", "");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void html_확장자가_있는_로그인_페이지를_조회한다() throws URISyntaxException {
        var socket = new StubSocket("GET /login.html HTTP/1.1\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void html_확장자가_있는_경로로_로그인한다() throws URISyntaxException {
        var socket = post("/login.html", "account=gugu&password=password");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(socket.output()).contains("Location: /index.html");
        assertThat(SessionManager.getInstance().findSession(findSessionId(socket)).getAttribute("user"))
                .isEqualTo(InMemoryUserRepository.findByAccount("gugu").orElseThrow());
    }

    @Test
    void 회원가입_페이지를_GET으로_조회한다() throws URISyntaxException {
        var socket = new StubSocket("GET /register HTTP/1.1\r\nHost: localhost\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("<title>회원가입</title>");
    }

    @Test
    void POST로_회원가입하면_저장하고_index로_리다이렉트한다() throws URISyntaxException {
        var socket = post("/register",
                "account=new-user&password=password&email=new%40example.com");

        new Http11Processor(socket).process(socket);

        assertThat(InMemoryUserRepository.findByAccount("new-user")).isPresent();
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @Test
    void 이미_존재하는_아이디로_가입하면_409로_응답한다() throws URISyntaxException {
        var socket = post("/register", "account=gugu&password=password&email=gugu%40example.com");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 409 Conflict\r\n");
        assertThat(socket.output()).endsWith("이미 존재하는 아이디입니다.");
    }

    @Test
    void CSS_파일을_올바른_ContentType으로_응답한다() throws URISyntaxException {
        var socket = new StubSocket("GET /css/styles.css HTTP/1.1\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("Content-Type: text/css;charset=utf-8\r\n");
    }

    @Test
    void html_확장자가_있는_회원가입_페이지를_조회한다() {
        var socket = new StubSocket("GET /register.html HTTP/1.1\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("<title>회원가입</title>");
    }

    @Test
    void html_확장자가_있는_경로로_회원가입한다() {
        var socket = post("/register.html", "account=html-user&password=password&email=html%40example.com");

        new Http11Processor(socket).process(socket);

        assertThat(InMemoryUserRepository.findByAccount("html-user")).isPresent();
        assertThat(socket.output()).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @Test
    void 정적_파일의_POST_요청도_파일을_응답한다() {
        var socket = post("/css/styles.css", "");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("Content-Type: text/css;charset=utf-8\r\n");
    }

    @Test
    void GET_POST_외_로그인_요청은_파일을_응답한다() {
        var socket = new StubSocket("PUT /login HTTP/1.1\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("<title>로그인</title>");
    }

    @Test
    void JS_파일을_올바른_ContentType으로_응답한다() throws URISyntaxException {
        var socket = new StubSocket("GET /js/scripts.js HTTP/1.1\r\n\r\n");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(socket.output()).contains("Content-Type: text/javascript;charset=utf-8\r\n");
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
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
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
        var expected = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/html;charset=utf-8\r\n" +
                "Content-Length: 5564\r\n" +
                "\r\n"+
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(socket.output()).isEqualTo(expected);
    }
}
