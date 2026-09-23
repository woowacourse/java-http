package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorTest {

    @Test
    void 로그인에_성공하면_인증된_User_객체를_세션의_user_속성에_저장한다() {
        // given
        String httpRequest = formRequest("/login", "account=usher&password=password");
        User user = InMemoryUserRepository.findByAccount("usher").orElseThrow();

        // when
        String response = process(httpRequest);

        // then
        String sessionId = getSessionCookie(response).substring("JSESSIONID=".length());
        Session session = SessionManager.find(sessionId).orElseThrow();
        assertThat(session.getAttribute("user")).isSameAs(user);
    }

    @Test
    void 등록되지_않은_JSESSIONID로_로그인_페이지에_접근하면_로그인_페이지를_응답한다() throws IOException {
        // given
        String httpRequest = "GET /login HTTP/1.1\r\nCookie: JSESSIONID=unknown-session-id\r\n\r\n";
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = Files.readString(new File(resource.getFile()).toPath());

        // when
        String response = process(httpRequest);

        // then
        assertThat(response).startsWith("HTTP/1.1 200 OK \r\n")
                .endsWith(responseBody)
                .doesNotContain("Set-Cookie");
        assertThat(SessionManager.find("unknown-session-id")).isEmpty();
    }

    @Test
    void 등록되지_않은_JSESSIONID로_로그인에_성공하면_새_JSESSIONID_쿠키를_발급한다() {
        // given
        String httpRequest = formRequest("/login", "account=usher&password=password",
                "JSESSIONID=unknown-session-id");

        // when
        String response = process(httpRequest);

        // then
        String sessionId = getSessionCookie(response).substring("JSESSIONID=".length());
        assertThat(UUID.fromString(sessionId)).isNotNull();
        assertThat(SessionManager.find(sessionId)).isPresent();
        assertThat(SessionManager.find("unknown-session-id")).isEmpty();
    }

    @Test
    void 세션_없이_로그인에_성공하면_JSESSIONID_쿠키를_발급한다() {
        // given
        String httpRequest = formRequest("/login", "account=usher&password=password", "theme=dark");

        // when
        String response = process(httpRequest);

        // then
        assertThat(response).containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]+ \\r\\n");
    }

    @Test
    void 기존_세션으로_로그인에_성공하면_해당_세션을_재사용한다() {
        // given
        Session session = SessionManager.create();
        String httpRequest = formRequest("/login", "account=usher&password=password",
                "JSESSIONID=" + session.getId());

        // when
        String response = process(httpRequest);

        // then
        assertThat(response).doesNotContain("Set-Cookie");
        assertThat(SessionManager.find(session.getId()).orElseThrow()).isSameAs(session);
        assertThat(session.getAttribute("user"))
                .isSameAs(InMemoryUserRepository.findByAccount("usher").orElseThrow());
    }

    @Test
    void 로그인한_세션으로_로그인_페이지에_접근하면_index_페이지로_리다이렉트한다() {
        // given
        String firstResponse = process(formRequest("/login", "account=usher&password=password"));
        String sessionCookie = getSessionCookie(firstResponse);
        String loginBody = "account=usher&password=password";
        process(formRequest("/login", loginBody, "theme=dark; " + sessionCookie));
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Cookie: locale=ko; " + sessionCookie,
                "",
                "");

        // when
        String response = process(httpRequest);

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n")
                .doesNotContain("Set-Cookie");
    }

    @Test
    void 세션_없이_로그인_페이지에_접근하면_로그인_페이지를_응답한다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        // when
        String response = process(httpRequest);

        // then
        URL resource = getClass().getClassLoader().getResource("static/login.html");
        String responseBody = Files.readString(new File(resource.getFile()).toPath());
        assertThat(response)
                .startsWith("HTTP/1.1 200 OK \r\n")
                .doesNotContain("Set-Cookie")
                .endsWith(responseBody);
    }

    @Test
    void 로그인에_성공하면_index_페이지로_리다이렉트한다() {
        // given
        String body = "account=usher&password=password";
        String httpRequest = formRequest("/login", body);

        // when
        String response = process(httpRequest);

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n");
    }

    @Test
    void 존재하지_않는_계정으로_로그인하면_401_페이지로_리다이렉트한다() {
        // given
        String body = "account=unknown&password=password";
        String httpRequest = formRequest("/login", body);

        // when
        String response = process(httpRequest);

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /401.html \r\n")
                .doesNotContain("Set-Cookie");
    }

    @Test
    void 비밀번호가_틀리면_401_페이지로_리다이렉트한다() {
        // given
        String body = "account=usher&password=wrong";
        String httpRequest = formRequest("/login", body);

        // when
        String response = process(httpRequest);

        // then
        assertThat(response)
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /401.html \r\n")
                .doesNotContain("Set-Cookie");
    }

    @Test
    void GET_register는_register_html을_응답한다() throws IOException {
        // given
        String httpRequest = String.join("\r\n",
                "GET /register HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        URL resource = getClass().getClassLoader().getResource("static/register.html");
        String responseBody = Files.readString(new File(resource.getFile()).toPath());
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .endsWith(responseBody);
    }

    @Test
    void 회원가입에_성공하면_사용자를_저장하고_index_페이지로_리다이렉트한다() {
        // given
        String account = "usher-" + UUID.randomUUID();
        String body = "account=" + account + "&password=password&email=usher%40woowahan.com";
        String httpRequest = String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        User savedUser = InMemoryUserRepository.findByAccount(account).orElseThrow();
        assertThat(savedUser.checkPassword("password")).isTrue();
        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found \r\n")
                .contains("Location: /index.html \r\n")
                .doesNotContain("Set-Cookie")
                .endsWith("\r\n\r\n");
    }

    @Test
    void 존재하지_않는_파일을_요청하면_404_상태와_오류_페이지를_응답한다() throws IOException {
        // given
        final var socket = new StubSocket("GET /missing.html HTTP/1.1\r\n\r\n");
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output())
                .startsWith("HTTP/1.1 404 Not Found \r\n")
                .contains("Content-Type: text/html;charset=utf-8 \r\n")
                .doesNotContain("Set-Cookie")
                .endsWith(new String(getClass().getResourceAsStream("/static/404.html").readAllBytes(), StandardCharsets.UTF_8));
    }

    @Test
    void 요청_첫_줄이_없어도_예외가_발생하지_않는다() {
        // given
        final var socket = new StubSocket("");
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        assertThat(socket.output()).startsWith("HTTP/1.1 404 Not Found");
    }

    @Test
    void 로그인하지_않아도_정적_파일을_요청하면_해당_파일을_응답한다() throws IOException {
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
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Type: text/html;charset=utf-8 \r\n")
                .contains("Content-Length: 5564 \r\n")
                .doesNotContain("Set-Cookie")
                .endsWith("\r\n\r\n" + responseBody);
    }

    @Test
    void 로그인하지_않아도_CSS_파일을_요청하면_해당_파일을_응답한다() throws IOException {
        // given
        final String httpRequest = String.join("\r\n",
                "GET /css/styles.css HTTP/1.1 ",
                "Host: localhost:8080 ",
                "",
                "");
        final var socket = new StubSocket(httpRequest);
        final var processor = new Http11Processor(socket);

        // when
        processor.process(socket);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/css/styles.css");
        final String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK \r\n")
                .contains("Content-Type: text/css \r\n")
                .contains("Content-Length: " + responseBody.getBytes().length + " \r\n")
                .doesNotContain("Set-Cookie")
                .endsWith("\r\n\r\n" + responseBody);
    }

    private String formRequest(String path, String body) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "",
                body);
    }

    private String formRequest(String path, String body, String cookie) {
        return String.join("\r\n",
                "POST " + path + " HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length,
                "Cookie: " + cookie,
                "",
                body);
    }

    private String getSessionCookie(String response) {
        return response.lines()
                .filter(line -> line.startsWith("Set-Cookie: "))
                .map(line -> line.substring("Set-Cookie: ".length()).trim())
                .findFirst()
                .orElseThrow();
    }

    private String process(String httpRequest) {
        StubSocket socket = new StubSocket(httpRequest);
        Http11Processor processor = new Http11Processor(socket);
        processor.process(socket);
        return socket.output();
    }
}
