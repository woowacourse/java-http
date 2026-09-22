package com.techcourse.controller;

import com.techcourse.model.User;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static support.HttpRequestFixture.get;
import static support.HttpRequestFixture.getWithCookie;
import static support.HttpRequestFixture.post;

class LoginControllerTest {

    private final SessionManager sessionManager = new SessionManager();
    private final LoginController loginController = new LoginController(sessionManager);

    @Test
    @DisplayName("로그인하지 않은 사용자가 GET으로 요청하면 로그인 페이지를 반환한다.")
    void getLoginPage() throws IOException {
        assertThat(loginController.doService(get("/login"), new HttpResponse())).isEqualTo("/login.html");
    }

    @Test
    @DisplayName("존재하지 않는 세션으로 GET 요청하면 로그인 페이지를 반환한다.")
    void getWithUnknownSession() throws IOException {
        // when
        String viewName = loginController.doService(
                getWithCookie("/login", "JSESSIONID=unknown-session"), new HttpResponse());

        // then
        assertThat(viewName).isEqualTo("/login.html");
    }

    @Test
    @DisplayName("이미 로그인한 사용자가 GET으로 요청하면 메인 페이지로 리다이렉트한다.")
    void getWhenLoggedIn() throws IOException {
        // given
        Session session = new Session("login-controller-session");
        session.setAttribute("user", new User("gugu", "password", "gugu@example.com"));
        sessionManager.add(session);

        // when
        String viewName = loginController.doService(
                getWithCookie("/login", "JSESSIONID=login-controller-session"), new HttpResponse());

        // then
        assertThat(viewName).isEqualTo("redirect:/index.html");
    }

    @Test
    @DisplayName("로그인에 성공하면 세션을 만들고 쿠키를 설정한 뒤 메인 페이지로 리다이렉트한다.")
    void loginSuccess() throws IOException {
        // given
        HttpResponse response = new HttpResponse();

        // when
        String viewName = loginController.doService(post("/login", "account=gugu&password=password"), response);

        // then
        assertThat(viewName).isEqualTo("redirect:/index.html");

        String sessionId = extractSessionId(write(response));
        assertThat(sessionManager.findSession(sessionId).getAttribute("user"))
                .isInstanceOfSatisfying(User.class, user -> assertThat(user.getAccount()).isEqualTo("gugu"));
    }

    @Test
    @DisplayName("비밀번호가 틀리면 401 페이지로 리다이렉트하고 쿠키를 설정하지 않는다.")
    void loginFailure() throws IOException {
        // given
        HttpResponse response = new HttpResponse();

        // when
        String viewName = loginController.doService(post("/login", "account=gugu&password=wrong"), response);

        // then
        assertThat(viewName).isEqualTo("redirect:/401.html");
        assertThat(write(response)).doesNotContain("Set-Cookie");
    }

    @Test
    @DisplayName("존재하지 않는 계정이면 401 페이지로 리다이렉트한다.")
    void loginWithUnknownAccount() throws IOException {
        assertThat(loginController.doService(post("/login", "account=nobody&password=password"), new HttpResponse()))
                .isEqualTo("redirect:/401.html");
    }

    private String extractSessionId(String response) {
        String prefix = "Set-Cookie: JSESSIONID=";
        return response.lines()
                .filter(line -> line.startsWith(prefix))
                .map(line -> line.substring(prefix.length()))
                .findFirst()
                .orElseThrow();
    }

    private String write(HttpResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.write(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

}
