package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpRequestStartLine;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.query.Query;
import org.apache.coyote.view.ViewResolver;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Nested
    class 로그인 {

        @Test
        void 쿼리가_없으면_로그인_페이지를_반환한다() {
            // given
            HttpRequestStartLine startLine = HttpRequestStartLine.create(
                    "GET /login HTTP/1.1 ");
            HttpRequest request = new HttpRequest(startLine, new HttpHeaders(), null);
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);

            // then
            Assertions.assertAll(
                    () -> assertThat(response.getCode()).isEqualTo(HttpStatus.OK.getCode()),
                    () -> assertThat(response.getView()).isEqualTo(ViewResolver.getView("login.html"))
            );
        }


        @Test
        void 로그인에_성공하면_302를_반환한다() {
            // given
            HttpRequestStartLine startLine = HttpRequestStartLine.create("POST /login HTTP/1.1 ");
            Query body = Query.create("account=gugu&password=password");
            HttpRequest request = new HttpRequest(startLine, new HttpHeaders(), body);
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
            HttpRequestStartLine startLine = HttpRequestStartLine.create("POST /login HTTP/1.1 ");
            Query body = Query.create("account=NoExist&password=NoExist");
            HttpRequest request = new HttpRequest(startLine, new HttpHeaders(), body);
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);

            // then
            assertThat(response.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
        }

        @Test
        void 로그인을_성공하면_응답_헤더에_쿠키를_포함한다() {
            // given
            HttpRequestStartLine startLine = HttpRequestStartLine.create("POST /login HTTP/1.1 ");
            Query body = Query.create("account=gugu&password=password");
            HttpRequest request = new HttpRequest(startLine, new HttpHeaders(), body);
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);
            // then
            assertThat(response.findHeaderByKey("Set-Cookie").isPresent()).isTrue();
        }

        @Test
        void 로그인을_성공하면_헤더에_쿠키를_포함한다() {
            // given
            HttpRequestStartLine startLine = HttpRequestStartLine.create("POST /login HTTP/1.1 ");
            Query body = Query.create("account=gugu&password=password");
            HttpRequest request = new HttpRequest(startLine, new HttpHeaders(), body);
            HttpResponse response = new HttpResponse();
            FrontController controller = FrontController.getInstance();

            // when
            controller.service(request, response);
            // then
            assertThat(response.findHeaderByKey("Set-Cookie").isPresent()).isTrue();
        }
    }
}
