package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("추상 컨트롤러")
class AbstractControllerTest {

    private static final HttpResponse GET_RESPONSE = HttpResponse.redirect("/get");
    private static final HttpResponse POST_RESPONSE = HttpResponse.redirect("/post");

    private final AbstractController controller = new AbstractController() {
        @Override
        protected HttpResponse doGet(final HttpRequest request) {
            return GET_RESPONSE;
        }

        @Override
        protected HttpResponse doPost(final HttpRequest request) {
            return POST_RESPONSE;
        }
    };

    @Test
    @DisplayName("GET 요청은 doGet으로 처리한다")
    void delegatesGetRequestToDoGet() throws Exception {
        // given
        final var request = request("GET");

        // when
        final var response = controller.service(request);

        // then
        assertThat(response).isSameAs(GET_RESPONSE);
    }

    @Test
    @DisplayName("POST 요청은 doPost로 처리한다")
    void delegatesPostRequestToDoPost() throws Exception {
        // given
        final var request = request("POST");

        // when
        final var response = controller.service(request);

        // then
        assertThat(response).isSameAs(POST_RESPONSE);
    }

    @Test
    @DisplayName("지원하지 않는 메서드에는 405 Method Not Allowed를 응답한다")
    void returnsMethodNotAllowedForUnsupportedMethod() throws Exception {
        // given
        final var request = request("PATCH");

        // when
        final var response = controller.service(request);

        // then
        assertThat(response.status()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
    }

    private HttpRequest request(final String method) throws Exception {
        final var rawRequest = String.join("\r\n",
                method + " / HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");
        return HttpRequest.readFrom(new BufferedReader(new StringReader(rawRequest))).orElseThrow();
    }
}
