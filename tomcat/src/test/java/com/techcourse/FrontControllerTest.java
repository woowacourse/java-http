package com.techcourse;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.apache.coyote.http11.HttpRequest;
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
            Map<String, String> queries = Map.of(
                    "account", "gugu",
                    "password", "password"
            );
            HttpRequest request = new HttpRequest("GET", "/login", "HTTP/1.1", null, queries, null);
            HttpResponse response = new HttpResponse();

            // when
            FrontController.service(request, response);

            // then
            assertThat(response.getCode()).isEqualTo(HttpStatus.FOUND.getCode());
        }

        @Test
        void 로그인에_실패하면_401를_반환한다() {
            // given
            Map<String, String> queries = Map.of(
                    "account", "NoExist",
                    "password", "NoExist"
            );
            HttpRequest request = new HttpRequest("GET", "/login", "HTTP/1.1", null, queries, null);
            HttpResponse response = new HttpResponse();

            // when
            FrontController.service(request, response);

            // then
            assertThat(response.getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
        }
    }
}