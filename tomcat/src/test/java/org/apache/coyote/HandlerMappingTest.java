package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.handler.Handler;
import org.apache.coyote.handler.LoginHandler;
import org.apache.coyote.handler.RegisterHandler;
import org.apache.coyote.handler.RootEndPointHandler;
import org.apache.coyote.handler.StaticResourceHandler;
import org.apache.coyote.handler.exception.InternalServerErrorHandler;
import org.apache.coyote.handler.exception.NotFoundHandler;
import org.apache.coyote.handler.exception.UnAuthorizationHandler;
import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HandlerMappingTest {

    @Nested
    class getHandler {
        @Test
        @DisplayName("루트 경로를 포함하는 경로일 경우: RootEndPointHandler 반환")
        void getHandler_RootPath_ReturnsRootEndPointHandler() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
            HttpRequest request = new HttpRequest(requestLine, null, null);
            Handler handler = HandlerMapping.getInstance().getHandler(request);
            assertThat(handler).isInstanceOf(RootEndPointHandler.class);
        }

        @Test
        @DisplayName("login을 포함하는 경로일 경우: LoginHandler 반환")
        void getHandler_LoginPath_ReturnsLoginHandler() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/login", HttpVersion.HTTP_1_1);
            HttpRequest request = new HttpRequest(requestLine, null, null);
            Handler handler = HandlerMapping.getInstance().getHandler(request);
            assertThat(handler).isInstanceOf(LoginHandler.class);
        }

        @Test
        @DisplayName("register 포함하는 경로일 경우: RegisterHandler를 반환")
        void getHandler_RegisterPath_ReturnsRegisterHandler() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/register", HttpVersion.HTTP_1_1);
            HttpRequest request = new HttpRequest(requestLine, null, null);
            Handler handler = HandlerMapping.getInstance().getHandler(request);
            assertThat(handler).isInstanceOf(RegisterHandler.class);
        }

        @Test
        @DisplayName("정적 리소스 경로: StaticResourceHandler 반환")
        void getHandler_StaticResourcePath_ReturnsStaticResourceHandler() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1);
            HttpRequest request = new HttpRequest(requestLine, null, null);
            Handler handler = HandlerMapping.getInstance().getHandler(request);
            assertThat(handler).isInstanceOf(StaticResourceHandler.class);
        }
    }

    @Nested
    class getHandlerByException {

        @Test
        @DisplayName("UnauthorizedException 처리 : UnAuthorizationHandler 반환")
        void getHandlerByException_UnauthorizedException() {
            Handler handler = HandlerMapping.getInstance().getHandlerByException(new UnauthorizedException("권한이 없습니다."));
            assertThat(handler).isInstanceOf(UnAuthorizationHandler.class);
        }

        @Test
        @DisplayName("UnauthorizedException 처리 : UnAuthorizationHandler 반환")
        void getHandlerByException_NotFoundException() {
            Handler handler = HandlerMapping.getInstance().getHandlerByException(new NotFoundException("존재하지 않는 리소스입니다."));
            assertThat(handler).isInstanceOf(NotFoundHandler.class);
        }

        @Test
        @DisplayName("알 수 없는 에러: InternalServerErrornHandler 반환")
        void getHandlerByException_UnknownPath_ReturnsStaticResourceHandler() {
            Handler handler = HandlerMapping.getInstance().getHandlerByException(new Exception("에러"));
            assertThat(handler).isInstanceOf(InternalServerErrorHandler.class);
        }
    }
}
