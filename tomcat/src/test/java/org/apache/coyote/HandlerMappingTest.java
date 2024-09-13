package org.apache.coyote;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.DefaultController;
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

import com.techcourse.controller.ControllerMapping;
import com.techcourse.controller.LoginController;
import com.techcourse.controller.RegisterController;
import com.techcourse.controller.RootEndPointController;
import com.techcourse.controller.exception.ExceptionMapping;
import com.techcourse.controller.exception.InternalServerErrorController;
import com.techcourse.controller.exception.NotFoundController;
import com.techcourse.controller.exception.UnAuthorizationController;

class HandlerMappingTest {

    @Nested
    class getHandler {
    }

    @Nested
    class getHandlerByException {

        @Test
        @DisplayName("UnauthorizedException 처리 : UnAuthorizationHandler 반환")
        void getHandlerByException_UnauthorizedException() {
            Controller handler = ExceptionMapping.getInstance()
                    .getHandler(new UnauthorizedException("권한이 없습니다."));
            assertThat(handler).isInstanceOf(UnAuthorizationController.class);
        }

        @Test
        @DisplayName("UnauthorizedException 처리 : UnAuthorizationHandler 반환")
        void getHandlerByException_NotFoundException() {
            Controller handler = ExceptionMapping.getInstance()
                    .getHandler(new NotFoundException("존재하지 않는 리소스입니다."));
            assertThat(handler).isInstanceOf(NotFoundController.class);
        }

        @Test
        @DisplayName("알 수 없는 에러: InternalServerErrornHandler 반환")
        void getHandlerByException_UnknownPath_ReturnsStaticResourceHandler() {
            Controller handler = ExceptionMapping.getInstance()
                    .getHandler(new Exception("에러"));
            assertThat(handler).isInstanceOf(InternalServerErrorController.class);
        }
    }
}
