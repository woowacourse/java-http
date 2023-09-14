package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RootControllerTest {
    private final Controller rootController = new RootController();
    private HttpRequest request;

    @BeforeEach
    void setUp() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Content-Type: text/html",
                "",
                "");
        final StringReader stringReader = new StringReader(rawRequest);
        request = HttpRequest.from(new BufferedReader(stringReader));
    }

    @Test
    @DisplayName("지원하지 않는 HttpMethod는 해당 컨트롤러가 처리할 수 없다.")
    void handleExceptionTest() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "POST / HTTP/1.1 ",
                "Content-Type: text/html",
                "",
                "");
        final StringReader stringReader = new StringReader(rawRequest);
        request = HttpRequest.from(new BufferedReader(stringReader));

        //when, then
        assertThatThrownBy(() -> rootController.service(request, HttpResponse.create()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("기본 메시지를 응답할 수 있다.")
    void requestTest() throws Exception {
        //when
        final HttpResponse response = HttpResponse.create();
        rootController.service(request, response);

        //then
        final String content = "Hello world!";
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.getBytes().length + " ",
                "",
                content);
        assertThat(response.toString()).isEqualTo(expected);
    }
}
