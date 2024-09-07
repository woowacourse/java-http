package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestStartLine;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Nested
    class 회원가입 {

        @Test
        void 페이지를_반환한다() {
            // given
            HttpRequestStartLine startLine = HttpRequestStartLine.createByString(
                    "GET /register HTTP/1.1 ");
            HttpRequest request = new HttpRequest(startLine, null, null);
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getCode()).isEqualTo(HttpStatus.OK.getCode()),
                    () -> assertThat(response.getView().getContent()).isEqualTo(ViewResolver.getView("register.html").getContent())
            );
        }

        @Test
        void 회원가입_POST_요청을_처리한다() {
            // given
            HttpRequestStartLine startLine = HttpRequestStartLine.createByString(
                    "POST /register HTTP/1.1 ");
            HttpRequest request = new HttpRequest(startLine, null, null);
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getCode()).isEqualTo(HttpStatus.FOUND.getCode())
            );
        }
    }
}
