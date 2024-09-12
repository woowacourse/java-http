package com.techcourse.controller;

import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LoginControllerTest {

    private static final Pattern JSESSIONID_PATTERN = Pattern.compile("Set-Cookie: JSESSIONID=[\\w-]+");
    private static final String LOCATION_401_HEADER = "Location: 401.html";
    private static final String LOCATION_MAIN_HEADER = "Location: /index.html";
    private static final String SET_COOKIE_JSESSIONID_HEADER = "Set-Cookie: JSESSIONID=";

    private final LoginController loginController;

    public LoginControllerTest() {
        this.loginController = new LoginController();
    }

    @Test
    @DisplayName("/login 으로 요청이 들어오는 경우 true를 반환한다.")
    void canHandle_WhenLoginPath() {
        HttpRequest httpRequest = HttpRequestFixture.GET_LOGIN_PATH_NO_COOKIE_REQUEST;

        assertTrue(loginController.canHandle(httpRequest));
    }

    @Test
    @DisplayName("/login 요청이 아닌 경우 false를 반환한다.")
    void canNotHandle_WhenNotLoginPath() {
        HttpRequest httpRequest = HttpRequestFixture.POST_REGISTER_PATH_REQUEST;

        assertFalse(loginController.canHandle(httpRequest));
    }

    @Test
    @DisplayName("로그인에 실패하는 경우 401 페이지로 리다이렉트 한다.")
    void doPost_WhenNotUser() {
        HttpRequest httpRequest = HttpRequestFixture.POST_LOGIN_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        loginController.doPost(httpRequest, httpResponse);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(LOCATION_401_HEADER));
    }

    @Test
    @DisplayName("로그인에 성공하는 경우 메인 페이지로 리다이렉트 한다.")
    void doPost_WhenUser() {
        HttpRequest httpRequest = HttpRequestFixture.POST_LOGGED_IN_USER_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        loginController.doPost(httpRequest, httpResponse);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(LOCATION_MAIN_HEADER));
    }

    @Test
    @DisplayName("로그인에 성공하는 경우 쿠키가 생성된다.")
    void setCookie_WhenUser() {
        HttpRequest httpRequest = HttpRequestFixture.POST_LOGGED_IN_USER_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        loginController.doPost(httpRequest, httpResponse);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(SET_COOKIE_JSESSIONID_HEADER));
    }

    @Test
    @DisplayName("쿠키가 있는 상태에서 로그인 페이지에 접속하는 경우 메인 페이지로 리다이렉트 한다.")
    void redirectMainPage_WhenCookieExist() {
        HttpRequest noCookieUserRequest = HttpRequestFixture.POST_LOGGED_IN_USER_PATH_REQUEST;
        HttpResponse noCookieUserResponse = new HttpResponse(OutputStream.nullOutputStream());
        loginController.doPost(noCookieUserRequest, noCookieUserResponse);

        Matcher cookieMatcher = JSESSIONID_PATTERN.matcher(new String(noCookieUserResponse.combineResponseToBytes()));

        String cookie = "";
        if (cookieMatcher.find()) {
            cookie = cookieMatcher.group();
        }

        HttpRequest cookieUserRequest = HttpRequestFixture.POST_LOGGED_IN_USER_WITH_COOKIE(cookie);
        HttpResponse cookieUserResponse = new HttpResponse(OutputStream.nullOutputStream());
        loginController.doPost(cookieUserRequest, cookieUserResponse);

        assertTrue(new String(cookieUserResponse.combineResponseToBytes()).contains(LOCATION_MAIN_HEADER));
    }
}
