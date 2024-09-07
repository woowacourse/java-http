package com.techcourse;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.FrontController;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestStartLine;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FrontControllerTest {

    @Nested
    class 로그인 {

        @Test
        void 로그인에_성공하면_302를_반환한다() {
            // given
            HttpRequestStartLine startLine = HttpRequestStartLine.createByString(
                    "GET /login?account=gugu&password=password HTTP/1.1 ");
            HttpRequest request = new HttpRequest(startLine, null, null);
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);

            // then
            assertThat(response.getCode()).isEqualTo(HttpStatus.FOUND.getCode());
        }

        @Test
        void 로그인에_실패하면_401를_반환한다() {
            // given
            HttpRequestStartLine startLine = HttpRequestStartLine.createByString(
                    "GET /login?account=NoExist&password=NoExist HTTP/1.1 ");
            HttpRequest request = new HttpRequest(startLine, null, null);
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);

            // then
            assertThat(response.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
        }
    }
}
