package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.view.ViewResolver;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class MainPageControllerTest {

    @Nested
    class 메인페이지 {

        @Test
        void 메인페이지_요청을_처리한다() {
            // given
            HttpRequest request = HttpRequest.create("GET / HTTP/1.1");
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);

            // then
            assertThat(response.getView()).isEqualTo(ViewResolver.getView("index.html"));
        }

        @Test
        void 존재하지_않는_요청시_에러가_발생한다() {
            // given
            HttpRequest request = HttpRequest.create("POST / HTTP/1.1");
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);

            // then
            assertThat(response.getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }
    }
}
