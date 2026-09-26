package com.techcourse.controller;

import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private static final String SESSION_ID = "login-controller-session";

    private final LoginController controller = new LoginController();

    @AfterEach
    void tearDown() {
        SessionManager.getInstance().remove(SESSION_ID);
    }

    @Test
    void 비로그인_상태에서_GET_요청이면_로그인_페이지를_응답한다() throws Exception {
        final HttpRequest request = HttpRequest.from("GET /login HTTP/1.1");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("<form method=\"post\" action=\"login\">");
    }

    @Test
    void 로그인_상태에서_GET_요청이면_index로_리다이렉트한다() throws Exception {
        final Session session = sessionWithUser();
        final HttpRequest request = requestWithSession("GET /login HTTP/1.1", "");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(session.getAttribute("user")).isInstanceOf(User.class);
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html\r\n");
    }

    @Test
    void 올바른_계정과_비밀번호로_POST_요청하면_로그인한다() throws Exception {
        final Session session = new Session(SESSION_ID);
        SessionManager.getInstance().add(session);
        final HttpRequest request = requestWithSession(
                "POST /login HTTP/1.1",
                "account=gugu&password=password"
        );
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(session.getAttribute("user")).isInstanceOf(User.class);
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /index.html\r\n");
    }

    @Test
    void 비밀번호가_일치하지_않으면_401로_리다이렉트한다() throws Exception {
        final HttpRequest request = postLoginRequest("gugu", "wrong");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /401.html\r\n");
    }

    @Test
    void 사용자가_존재하지_않으면_401로_리다이렉트한다() throws Exception {
        final HttpRequest request = postLoginRequest("unknown", "password");
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(outputStream);

        controller.service(request, response);

        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 302 Found\r\n")
                .contains("Location: /401.html\r\n");
    }

    private Session sessionWithUser() {
        final Session session = new Session(SESSION_ID);
        session.setAttribute("user", new User("gugu", "password", "gugu@example.com"));
        SessionManager.getInstance().add(session);
        return session;
    }

    private HttpRequest postLoginRequest(final String account, final String password) throws IOException {
        return requestFrom(
                "POST /login HTTP/1.1",
                "account=" + account + "&password=" + password,
                null
        );
    }

    private HttpRequest requestWithSession(final String requestLine, final String body) throws IOException {
        return requestFrom(requestLine, body, "JSESSIONID=" + SESSION_ID);
    }

    private HttpRequest requestFrom(final String requestLine, final String body,
                                    final String cookie) throws IOException {
        final StringBuilder httpRequest = new StringBuilder()
                .append(requestLine).append("\r\n")
                .append("Host: localhost:8080\r\n");
        if (cookie != null) {
            httpRequest.append("Cookie: ").append(cookie).append("\r\n");
        }
        if (!body.isEmpty()) {
            httpRequest.append("Content-Type: application/x-www-form-urlencoded\r\n")
                    .append("Content-Length: ")
                    .append(body.getBytes(StandardCharsets.UTF_8).length)
                    .append("\r\n");
        }
        httpRequest.append("\r\n").append(body);

        return HttpRequest.from(new ByteArrayInputStream(
                httpRequest.toString().getBytes(StandardCharsets.UTF_8)
        ));
    }
}
