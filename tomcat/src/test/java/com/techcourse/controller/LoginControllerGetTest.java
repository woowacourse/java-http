package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerGetTest {

    private Session session;

    @AfterEach
    void tearDown() {
        if (session != null) {
            SessionManager.getInstance().remove(session);
        }
    }

    @Test
    void 로그인하지_않은_사용자에게_로그인_페이지를_응답한다() throws Exception {
        // given
        final LoginController loginController = new LoginController();
        final HttpRequest request = createRequest(Map.of());

        // when
        final HttpResponse response = new HttpResponse();
        loginController.service(request, response);

        // then
        assertThat(response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.headers()).doesNotContainKey("Location");
    }

    @Test
    void 로그인한_사용자에게_인덱스_페이지_위치를_응답한다() throws Exception {
        // given
        session = new Session("login-page-session");
        session.setAttribute(
                "user",
                new User("account", "password", "email@example.com")
        );
        SessionManager.getInstance().add(session);

        final LoginController loginController = new LoginController();
        final HttpRequest request = createRequest(Map.of(
                "cookie", "JSESSIONID=" + session.getId()
        ));

        // when
        final HttpResponse response = new HttpResponse();
        loginController.service(request, response);

        // then
        assertThat(response.status()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.headers())
                .containsEntry("Location", "/index.html");
    }

    @Test
    void 유효하지_않은_세션이면_로그인_페이지를_응답한다() throws Exception {
        // given
        final LoginController loginController = new LoginController();
        final HttpRequest request = createRequest(Map.of(
                "cookie", "JSESSIONID=unknown-session"
        ));

        // when
        final HttpResponse response = new HttpResponse();
        loginController.service(request, response);

        // then
        assertThat(response.status()).isEqualTo(HttpStatus.OK);
        assertThat(response.headers()).doesNotContainKey("Location");
    }

    private HttpRequest createRequest(final Map<String, String> headers) {
        return HttpRequest.builder()
                .httpMethod(HttpMethod.GET)
                .path("/login")
                .version("HTTP/1.1")
                .headers(headers)
                .build();
    }
}
