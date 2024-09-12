package com.techcourse.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.OutputStream;
import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.util.ResourceFinder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    private static final String REGISTER_RESOURCE_PATH = "/register.html";
    private static final String LOCATION_REGISTER_HEADER = "Location: /register";
    private static final String LOCATION_MAIN_HEADER = "Location: /index.html";

    private final RegisterController registerController;

    public RegisterControllerTest() {
        this.registerController = new RegisterController();
    }

    @Test
    @DisplayName("/register 로 요청이 들어오는 경우 true를 반환다.")
    void canHandle_WhenRegisterPath() {
        HttpRequest httpRequest = HttpRequestFixture.POST_REGISTER_PATH_REQUEST;

        assertTrue(registerController.canHandle(httpRequest));
    }

    @Test
    @DisplayName("/register가 아닌 요청이 들어오는 경우 false를 반환한다.")
    void canNotHandle_WhenNotRegisterPath() {
        HttpRequest httpRequest = HttpRequestFixture.POST_LOGIN_PATH_REQUEST;

        assertFalse(registerController.canHandle(httpRequest));
    }

    @Test
    @DisplayName("/register 요청이 들어오는 경우 /register 페이지를 반환한다.")
    void responseRegisterPage_WhenGetRegister() {
        HttpRequest httpRequest = HttpRequestFixture.GET_REGISTER_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        registerController.doGet(httpRequest, httpResponse);

        String resource = ResourceFinder.findBy(REGISTER_RESOURCE_PATH);
        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(resource));
    }

    @Test
    @DisplayName("이미 가입한 유저가 회원가입 하는 경우, 회원가입 페이지에 머물러 있는다.")
    void redirectRegisterPage_WhenRegisteredUser() {
        HttpRequest httpRequest = HttpRequestFixture.POST_REGISTER_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        registerController.doPost(httpRequest, httpResponse);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(LOCATION_REGISTER_HEADER));
    }

    @Test
    @DisplayName("새로운 유저가 회원가입 하는 경우, 메인 페이지로 리다이렉트 한다.")
    void redirectMainPage_WhenNewUser() {
        HttpRequest httpRequest = HttpRequestFixture.POST_NEW_USER_REGISTER_PATH_REQUEST;
        HttpResponse httpResponse = new HttpResponse(OutputStream.nullOutputStream());

        registerController.doPost(httpRequest, httpResponse);

        assertTrue(new String(httpResponse.combineResponseToBytes()).contains(LOCATION_MAIN_HEADER));
    }
}
