package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.StaticResourceController;
import org.apache.coyote.exception.NotFoundException;
import org.apache.coyote.exception.UnauthorizedException;
import org.apache.http.HttpMethod;
import org.apache.http.HttpVersion;
import org.apache.http.header.HttpHeaders;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootEndPointController;
import com.techcourse.controller.exception.InternalServerErrorController;
import com.techcourse.controller.exception.NotFoundController;
import com.techcourse.controller.exception.UnAuthorizationController;

class HandlerMappingTest {

    @Nested
    class getHandler {
        @Test
        @DisplayName("루트 경로를 포함하는 경로일 경우: RootEndPointHandler 반환")
        void getHandler_RootPath_ReturnsRootEndPointHandler() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/", HttpVersion.HTTP_1_1);
            HttpRequest request = new HttpRequest(requestLine, new HttpHeaders(), null);
            Controller handler = HandlerMapping.getInstance().getHandler(request);
            assertThat(handler).isInstanceOf(RootEndPointController.class);
        }

        @Test
        @DisplayName("login을 포함하는 경로일 경우: LoginHandler 반환")
        void getHandler_LoginPath_ReturnsLoginHandler() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/login", HttpVersion.HTTP_1_1);
            HttpRequest request = new HttpRequest(requestLine, new HttpHeaders(), null);
            Controller handler = HandlerMapping.getInstance().getHandler(request);
            assertThat(handler).isInstanceOf(LoginController.class);
        }

        @Test
        @DisplayName("register 포함하는 경로일 경우: RegisterHandler를 반환")
        void getHandler_RegisterPath_ReturnsRegisterHandler() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/register", HttpVersion.HTTP_1_1);
            HttpRequest request = new HttpRequest(requestLine, new HttpHeaders(), null);
            Controller handler = HandlerMapping.getInstance().getHandler(request);
            assertThat(handler).isInstanceOf(RegisterController.class);
        }

        @Test
        @DisplayName("정적 리소스 경로: StaticResourceHandler 반환")
        void getHandler_StaticResourcePath_ReturnsStaticResourceHandler() {
            RequestLine requestLine = new RequestLine(HttpMethod.GET, "/index.html", HttpVersion.HTTP_1_1);
            HttpRequest request = new HttpRequest(requestLine, new HttpHeaders(), null);
            Controller handler = HandlerMapping.getInstance().getHandler(request);
            assertThat(handler).isInstanceOf(StaticResourceController.class);
        }
    }

    @Nested
    class getHandlerByException {

        @Test
        @DisplayName("UnauthorizedException 처리 : UnAuthorizationHandler 반환")
        void getHandlerByException_UnauthorizedException() {
            Controller handler = HandlerMapping.getInstance()
                    .getHandlerByException(new UnauthorizedException("권한이 없습니다."));
            assertThat(handler).isInstanceOf(UnAuthorizationController.class);
        }

        @Test
        @DisplayName("UnauthorizedException 처리 : UnAuthorizationHandler 반환")
        void getHandlerByException_NotFoundException() {
            Controller handler = HandlerMapping.getInstance()
                    .getHandlerByException(new NotFoundException("존재하지 않는 리소스입니다."));
            assertThat(handler).isInstanceOf(NotFoundController.class);
        }

        @Test
        @DisplayName("알 수 없는 에러: InternalServerErrornHandler 반환")
        void getHandlerByException_UnknownPath_ReturnsStaticResourceHandler() {
            Controller handler = HandlerMapping.getInstance().getHandlerByException(new Exception("에러"));
            assertThat(handler).isInstanceOf(InternalServerErrorController.class);
        }
    }
}
