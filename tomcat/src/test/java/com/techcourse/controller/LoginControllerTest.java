package com.techcourse.controller;

import com.techcourse.model.User;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.Cookies;
import org.apache.coyote.http11.request.FormContents;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    @Test
    @DisplayName("로그인에 성공하면 새 세션에 사용자를 저장하고 메인 페이지로 리다이렉트한다.")
    void redirectToIndexWhenLoginSucceeds() throws Exception {
        // given
        HttpRequest request = postLoginRequest("account=gugu&password=password");
        request.setSessionId("before-login-session");
        SessionManager.add(new Session("before-login-session"));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LoginController controller = new LoginController();

        // when
        controller.service(request, new HttpResponse(outputStream));

        // then
        String response = outputStream.toString(StandardCharsets.UTF_8);
        String sessionId = extractJSessionId(response);
        assertThat(response)
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /index.html");
        assertThat(SessionManager.findSession(sessionId).getAttribute("user"))
                .isInstanceOf(User.class);
        assertThat(SessionManager.findSession("before-login-session"))
                .isNull();
    }

    @Test
    @DisplayName("로그인에 실패하면 401 페이지로 리다이렉트한다.")
    void redirectToUnauthorizedWhenLoginFails() throws Exception {
        // given
        HttpRequest request = postLoginRequest("account=gugu&password=wrong");
        request.setSessionId("anonymous-session");
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        LoginController controller = new LoginController();

        // when
        controller.service(request, new HttpResponse(outputStream));

        // then
        assertThat(outputStream.toString(StandardCharsets.UTF_8))
                .contains("HTTP/1.1 302 Found")
                .contains("Location: /401.html");
    }

    private HttpRequest postLoginRequest(String body) {
        return new HttpRequest(
                new RequestLine("POST /login HTTP/1.1"),
                HttpHeaders.empty(),
                FormContents.from(body),
                Cookies.from(null)
        );
    }

    private String extractJSessionId(String response) {
        return response.lines()
                .filter(line -> line.startsWith("Set-Cookie: JSESSIONID="))
                .map(line -> line.substring("Set-Cookie: JSESSIONID=".length()))
                .map(value -> value.split(";", 2)[0])
                .findFirst()
                .orElseThrow();
    }
}
