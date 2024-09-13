package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.DefaultController;
import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;
import org.apache.http.header.HttpHeaders;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ControllerMappingTest {

    @Test
    @DisplayName("루트 경로를 포함하는 경로일 경우: RootEndPointHandler 반환")
    void getHandler_RootPath_ReturnsRootEndPointHandler() {
        RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
        HttpRequest request = new HttpRequest(requestLine, new HttpHeaders(), null);
        Controller handler = ControllerMapping.getInstance().getHandler(request);
        assertThat(handler).isInstanceOf(RootEndPointController.class);
    }

    @Test
    @DisplayName("login을 포함하는 경로일 경우: LoginHandler 반환")
    void getHandler_LoginPath_ReturnsLoginHandler() {
        RequestLine requestLine = new RequestLine(HttpMethod.GET, "/login", HttpVersion.HTTP_1_1);
        HttpRequest request = new HttpRequest(requestLine, new HttpHeaders(), null);
        Controller handler = ControllerMapping.getInstance().getHandler(request);
        assertThat(handler).isInstanceOf(LoginController.class);
    }

    @Test
    @DisplayName("register 포함하는 경로일 경우: RegisterHandler를 반환")
    void getHandler_RegisterPath_ReturnsRegisterHandler() {
        RequestLine requestLine = new RequestLine(HttpMethod.GET, "/register", HttpVersion.HTTP_1_1);
        HttpRequest request = new HttpRequest(requestLine, new HttpHeaders(), null);
        Controller handler = ControllerMapping.getInstance().getHandler(request);
        assertThat(handler).isInstanceOf(RegisterController.class);
    }

    @Test
    @DisplayName("정적 리소스 경로: StaticResourceHandler 반환")
    void getHandler_StaticResourcePath_ReturnsStaticResourceHandler() {
        RequestLine requestLine = new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1);
        HttpRequest request = new HttpRequest(requestLine, new HttpHeaders(), null);
        Controller handler = ControllerMapping.getInstance().getHandler(request);
        assertThat(handler).isInstanceOf(DefaultController.class);
    }
}
