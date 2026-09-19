package org.apache.coyote.http11;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import support.StubSocket;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class Http11ProcessorLoginTest {

    @AfterEach
    void tearDown() {
        SessionManager.getInstance().remove("login-session-id");
    }

    @Test
    void GET_방식으로_로그인_페이지를_조회한다() {
        final String httpRequest = String.join("\r\n",
                "GET /login HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        final var socket = new StubSocket(httpRequest);

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 200 OK")
                .contains("<form method=\"post\" action=\"login\">");
    }

    @Test
    void POST_로그인에_성공하면_index로_리다이렉트한다() {
        final StubSocket socket = postLoginRequest("gugu", "password");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
    }

    @Test
    void 로그인에_성공하면_Session에_User를_저장한다() {
        final Session session = new Session("login-session-id");
        SessionManager.getInstance().add(session);
        final StubSocket socket = postLoginRequest(
                "gugu",
                "password",
                "JSESSIONID=login-session-id"
        );

        new Http11Processor(socket).process(socket);

        assertThat(session.getAttribute("user")).isInstanceOf(User.class);
    }

    @Test
    void 비밀번호가_일치하지_않으면_401로_리다이렉트한다() {
        final StubSocket socket = postLoginRequest("gugu", "wrong");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found")
                .contains("Location: /401.html");
    }

    @Test
    void 사용자가_존재하지_않으면_401로_리다이렉트한다() {
        final StubSocket socket = postLoginRequest("unknown", "password");

        new Http11Processor(socket).process(socket);

        assertThat(socket.output())
                .startsWith("HTTP/1.1 302 Found")
                .contains("Location: /401.html");
    }

    private StubSocket postLoginRequest(final String account, final String password) {
        return postLoginRequest(account, password, null);
    }

    private StubSocket postLoginRequest(final String account, final String password, final String cookie) {
        final String requestBody = "account=" + account + "&password=" + password;
        final StringBuilder httpRequest = new StringBuilder()
                .append("POST /login HTTP/1.1\r\n")
                .append("Host: localhost:8080\r\n")
                .append("Content-Type: application/x-www-form-urlencoded\r\n")
                .append("Content-Length: ")
                .append(requestBody.getBytes(StandardCharsets.UTF_8).length)
                .append("\r\n");
        if (cookie != null) {
            httpRequest.append("Cookie: ").append(cookie).append("\r\n");
        }
        httpRequest.append("\r\n").append(requestBody);
        return new StubSocket(httpRequest.toString());
    }
}
