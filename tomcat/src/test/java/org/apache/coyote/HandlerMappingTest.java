package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.handler.RootEndPointHandler;
import org.apache.coyote.handler.StaticResourceHandler;
import org.apache.http.request.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HandlerMappingTest {

    private HandlerMapping requestHandlerMapping;

    @BeforeEach
    void setUp() {
        requestHandlerMapping = new HandlerMapping();
    }

    @Test
    @DisplayName("루트 경로를 포함하는 경로일 경우: RootEndPointHandler 반환")
    void getHandler_RootPath_ReturnsRootEndPointHandler() {
        HttpRequest request = new HttpRequest("GET", "/", "HTTP/1.1", null, null);
        Handler handler = requestHandlerMapping.getHandler(request);
        assertThat(handler).isInstanceOf(RootEndPointHandler.class);
    }

    @Test
    @DisplayName("login을 포함하는 경로일 경우: LoginHandler 반환")
    void getHandler_LoginPath_ReturnsLoginHandler() {
        HttpRequest request = new HttpRequest("GET", "/login", "HTTP/1.1", null, null);
        Handler handler = requestHandlerMapping.getHandler(request);
        assertThat(handler).isInstanceOf(LoginHandler.class);
    }

    @Test
    @DisplayName("register 포함하는 경로일 경우: RegisterHandler를 반환")
    void getHandler_RegisterPath_ReturnsRegisterHandler() {
        HttpRequest request = new HttpRequest("GET", "/register", "HTTP/1.1", null, null);
        Handler handler = requestHandlerMapping.getHandler(request);
        assertThat(handler).isInstanceOf(RegisterHandler.class);
    }

    @Test
    @DisplayName("정적 리소스 경로: StaticResourceHandler 반환")
    void getHandler_StaticResourcePath_ReturnsStaticResourceHandler() {
        HttpRequest request = new HttpRequest("GET", "/index.html", "HTTP/1.1", null, null);
        Handler handler = requestHandlerMapping.getHandler(request);
        assertThat(handler).isInstanceOf(StaticResourceHandler.class);
    }

    @Test
    @DisplayName("알 수 없는 경로: StaticResourceHandler를 반환")
    void getHandler_UnknownPath_ReturnsStaticResourceHandler() {
        HttpRequest request = new HttpRequest("GET", "/unknown/path", "HTTP/1.1", null, null);
        Handler handler = requestHandlerMapping.getHandler(request);
        assertThat(handler).isInstanceOf(StaticResourceHandler.class);
    }
}
