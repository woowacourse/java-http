package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.handler.FrontRequestHandler;
import org.apache.coyote.http11.handler.ResponseEntity;
import org.apache.coyote.http11.http.HttpCookie;
import org.apache.coyote.http11.http.HttpHeaders;
import org.apache.coyote.http11.http.HttpMethod;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpStatus;
import org.apache.coyote.http11.http.RequestBody;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class FrontRequestHandlerTest {

    @Nested
    class handle {

        private FrontRequestHandler frontRequestHandler = new FrontRequestHandler();

        @Test
        @DisplayName("root 값을 요청하면 기본 페이지를 보여준다.")
        void rootHandler() {
            ResponseEntity responseEntity = frontRequestHandler.handle(new HttpRequest(HttpMethod.GET, "/"));
            assertThat(responseEntity.getBody()).isEqualTo("Hello world!");
        }

        @Test
        @DisplayName("requestHandler가 존재하면 해당 requestHandler가 ResponseEntity를 반환한다.")
        void existHandler() {
            ResponseEntity responseEntity = frontRequestHandler.handle(new HttpRequest(HttpMethod.GET, "/login"));
            assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("requestHanlder를 지원하지 않고, 해당 경로에 리소스가 존재하면 해당 리소스를 반환한다.")
        void existResource() {
            ResponseEntity responseEntity = frontRequestHandler.handle(new HttpRequest(HttpMethod.GET, "/register"));
            assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("requestHandler에서 지원하지 않고 리소스에도 존재하지 않으면 404 NotFound 페이지를 반환한다.")
        void noExistResource() {
            ResponseEntity responseEntity = frontRequestHandler.handle(
                    new HttpRequest(HttpMethod.GET, "/nowhere", new HttpHeaders(), RequestBody.of("key=value"),
                            HttpCookie.of("JSESSION", "ASDF")));
            assertThat(responseEntity.getStatus()).isEqualTo(HttpStatus.NOTFOUND);
        }
    }
}
