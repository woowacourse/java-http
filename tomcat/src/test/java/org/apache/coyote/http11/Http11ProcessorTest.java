package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

class Http11ProcessorTest {

    private static final String SESSION_COOKIE = "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ";

    @Test
    @DisplayName("루트 경로로 요청하면 index.html을 응답한다")
    void index() throws IOException, URISyntaxException {
        String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                SESSION_COOKIE,
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).contains("HTTP/1.1 200 OK");
        assertThat(socket.output()).contains(readResource("static/index.html"));
    }

    @Test
    @DisplayName("미로그인 상태로 /login에 접근하면 로그인 페이지를 응답한다")
    void loginPage() throws IOException, URISyntaxException {
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                SESSION_COOKIE,
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).contains("HTTP/1.1 200 OK");
        assertThat(socket.output()).contains(readResource("static/login.html"));
    }

    @Test
    @DisplayName("로그인에 성공하면 index.html로 리다이렉트한다")
    void loginSuccess() {
        String body = "account=gugu&password=password";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes().length + " ",
                SESSION_COOKIE,
                "",
                body);
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).contains("HTTP/1.1 302 Found");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @Test
    @DisplayName("비밀번호가 틀리면 401.html로 리다이렉트한다")
    void loginFail() {
        String body = "account=gugu&password=wrong";
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes().length + " ",
                SESSION_COOKIE,
                "",
                body);
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).contains("HTTP/1.1 302 Found");
        assertThat(socket.output()).contains("Location: /401.html");
    }

    @Test
    @DisplayName("로그인한 상태로 /login에 접근하면 index.html로 리다이렉트한다")
    void alreadyLoggedIn() {
        String body = "account=gugu&password=password";
        String loginRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
        StubSocket loginSocket = new StubSocket(loginRequest);
        new Http11Processor(loginSocket).process(loginSocket);

        String sessionId = extractSessionId(loginSocket.output());

        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Cookie: JSESSIONID=" + sessionId + " ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).contains("HTTP/1.1 302 Found");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @Test
    @DisplayName("/register로 요청하면 회원가입 페이지를 응답한다")
    void registerPage() throws IOException, URISyntaxException {
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                SESSION_COOKIE,
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).contains("HTTP/1.1 200 OK");
        assertThat(socket.output()).contains(readResource("static/register.html"));
    }

    @Test
    @DisplayName("회원가입에 성공하면 index.html로 리다이렉트한다")
    void registerSuccess() {
        String body = "account=newuser&password=password&email=new@test.com";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Content-Length: " + body.getBytes().length + " ",
                SESSION_COOKIE,
                "",
                body);
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).contains("HTTP/1.1 302 Found");
        assertThat(socket.output()).contains("Location: /index.html");
    }

    @Test
    @DisplayName("존재하지 않는 경로로 요청하면 404 페이지를 응답한다")
    void notFound() throws IOException, URISyntaxException {
        String httpRequest = String.join("\r\n",
                "GET /unknown.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                SESSION_COOKIE,
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).contains("HTTP/1.1 404 Not Found");
        assertThat(socket.output()).contains(readResource("static/404.html"));
    }

    @Test
    @DisplayName("JSESSIONID 쿠키가 없으면 Set-Cookie를 응답한다")
    void setCookieWhenNoSession() {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).contains("Set-Cookie: JSESSIONID=");
    }

    @Test
    @DisplayName("JSESSIONID 쿠키가 있으면 Set-Cookie를 응답하지 않는다")
    void noSetCookieWhenSessionExists() {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                SESSION_COOKIE,
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output()).doesNotContain("Set-Cookie");
    }

    private String readResource(String path) throws IOException, URISyntaxException {
        URL url = getClass().getClassLoader().getResource(path);
        return new String(Files.readAllBytes(Paths.get(url.toURI())));
    }

    private String extractSessionId(String response) {
        int start = response.indexOf("JSESSIONID=") + "JSESSIONID=".length();
        int end = response.indexOf("\r\n", start);
        return response.substring(start, end).trim();
    }
}
