package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.data.ContentType;
import org.apache.coyote.http11.data.HttpCookie;
import org.apache.coyote.http11.data.HttpMethod;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpRequestParameter;
import org.apache.coyote.http11.data.HttpResponse;
import org.apache.coyote.http11.data.HttpStatusCode;
import org.apache.coyote.HttpVersion;
import org.apache.coyote.http11.data.MediaType;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {
    private final LoginController loginController = LoginController.getInstance();

    @Test
    @DisplayName("[POST] 로그인한다.")
    void login() {
        // given
        String requestBody = "account=gugu&password=password";
        HttpRequestParameter requestParameter = new HttpRequestParameter(
                Map.of("account", "gugu", "password", "password"));
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/login", HttpVersion.HTTP_1_1, requestBody,
                new ContentType(MediaType.URLENC, null), requestBody.length(), requestParameter, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        loginController.doPost(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(httpResponse.getRedirectUrl()).isEqualTo("/index.html");
            assertThat(httpResponse.getHttpCookies()).hasSize(1)
                    .extracting(HttpCookie::getName).containsExactly(HttpRequest.SESSION_ID_COOKIE_KEY);
        });
    }

    @Test
    @DisplayName("[POST] 존재하지 않는 계정으로 로그인한다.")
    void loginWithNotExistingAccount() {
        // given
        String requestBody = "account=mia&password=password";
        HttpRequestParameter requestParameter = new HttpRequestParameter(
                Map.of("account", "mia", "password", "password"));
        HttpRequest httpRequest = new HttpRequest(HttpMethod.POST, "/login", HttpVersion.HTTP_1_1, requestBody,
                new ContentType(MediaType.URLENC, null), requestBody.length(), requestParameter, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        loginController.doPost(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(httpResponse.getRedirectUrl()).isEqualTo("/401.html");
        });
    }

    @Test
    @DisplayName("[GET] 로그인하지 않은 사용자가 로그인 페이지를 조회한다.")
    void getLoginPageWithNotLoginUser() throws IOException {
        // given
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/login", HttpVersion.HTTP_1_1, null,
                new ContentType(MediaType.HTML, "charset=utf-8"), 0, null, List.of());
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        loginController.doGet(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.OK);
            assertThat(httpResponse.getContentType().getMediaType()).isEqualTo(MediaType.HTML);
            assertThat(httpResponse.getResponseBody()).isNotEmpty();
        });
    }

    @Test
    @DisplayName("[GET] 로그인한 사용자가 로그인 페이지를 조회한다.")
    void getLoginPageWithLoginUser() throws IOException {
        // given
        String sessionId = "session-id";
        SessionManager.add(new Session(sessionId));
        HttpRequest httpRequest = new HttpRequest(HttpMethod.GET, "/login", HttpVersion.HTTP_1_1, null,
                new ContentType(MediaType.HTML, "charset=utf-8"), 0, null,
                List.of(new HttpCookie(HttpRequest.SESSION_ID_COOKIE_KEY, sessionId, Map.of())));
        HttpResponse httpResponse = new HttpResponse(HttpVersion.HTTP_1_1);

        // when
        loginController.doGet(httpRequest, httpResponse);

        // then
        assertAll(() -> {
            assertThat(httpResponse.getHttpStatusCode()).isEqualTo(HttpStatusCode.FOUND);
            assertThat(httpResponse.getRedirectUrl()).isEqualTo("/index.html");
        });
    }
}
