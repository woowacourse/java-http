package com.techcourse.controller;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerPostTest {

    private final List<String> sessionIds = new ArrayList<>();

    @AfterEach
    void tearDown() {
        final SessionManager sessionManager = SessionManager.getInstance();

        sessionIds.stream()
                .filter(sessionManager::isExistSession)
                .map(sessionManager::findSession)
                .forEach(sessionManager::remove);
    }

    @Test
    void 올바른_계정과_비밀번호면_302를_반환한다() throws Exception {
        // given
        final LoginController loginController =
                new LoginController();

        final HttpRequest request = createLoginRequest("gugu", "password");

        // when
        final HttpResponse response = new HttpResponse();
        loginController.service(request, response);

        sessionIds.add(getSessionId(response));

        // then
        assertThat(response.status())
                .isEqualTo(HttpStatus.FOUND);
        assertThat(response.headers())
                .containsEntry("Location", "/index.html");
    }

    @Test
    void 비밀번호가_일치하지_않으면_401_페이지로_리다이렉트한다() throws Exception {
        // given
        final LoginController loginController =
                new LoginController();

        final HttpRequest request = createLoginRequest("gugu", "wrong-password");

        // when
        final HttpResponse response = new HttpResponse();
        loginController.service(request, response);

        // then
        assertThat(response.status())
                .isEqualTo(HttpStatus.SEE_OTHER);
        assertThat(response.headers())
                .containsEntry("Location", "/401.html");
    }

    @Test
    void 재로그인하면_기존_세션을_제거하고_새로운_세션을_생성한다() throws Exception {
        // given
        final LoginController loginController = new LoginController();

        final HttpResponse firstResponse = new HttpResponse();
        loginController.service(
                createLoginRequest("gugu", "password"),
                firstResponse
        );

        final String oldSessionId = getSessionId(firstResponse);
        sessionIds.add(oldSessionId);

        final HttpRequest reLoginRequest = createLoginRequest(
                "gugu",
                "password",
                Map.of("cookie", "JSESSIONID=" + oldSessionId)
        );

        // when
        final HttpResponse secondResponse = new HttpResponse();
        loginController.service(reLoginRequest, secondResponse);

        final String newSessionId = getSessionId(secondResponse);
        sessionIds.add(newSessionId);

        // then
        final SessionManager sessionManager = SessionManager.getInstance();

        assertThat(newSessionId).isNotEqualTo(oldSessionId);
        assertThat(sessionManager.isExistSession(oldSessionId)).isFalse();
        assertThat(sessionManager.isExistSession(newSessionId)).isTrue();
    }

    private HttpRequest createLoginRequest(
            final String account,
            final String password
    ) {
        return createLoginRequest(account, password, Map.of());
    }

    private HttpRequest createLoginRequest(
            final String account,
            final String password,
            final Map<String, String> headers
    ) {
        return HttpRequest.builder()
                .httpMethod(HttpMethod.POST)
                .path("/login")
                .version("HTTP/1.1")
                .headers(headers)
                .params(Map.of(
                        "account", account,
                        "password", password
                ))
                .build();
    }

    private String getSessionId(final HttpResponse response) {
        final String setCookie = response.headers().get("Set-Cookie");
        final HttpCookie cookie = new HttpCookie(setCookie);

        return cookie.getSessionId();
    }
}
