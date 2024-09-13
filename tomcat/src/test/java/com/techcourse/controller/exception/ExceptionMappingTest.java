package com.techcourse.controller.exception;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.controller.Controller;
import org.apache.coyote.exception.NotFoundException;
import org.apache.coyote.exception.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ExceptionMappingTest {

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
