package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StaticResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IndexControllerTest {
    private final Controller indexController = new IndexController();
    private HttpRequest request;

    @BeforeEach
    void setUp() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
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
                "POST /index.html HTTP/1.1 ",
                "Content-Type: text/html",
                "",
                "");
        final StringReader stringReader = new StringReader(rawRequest);
        request = HttpRequest.from(new BufferedReader(stringReader));

        //when, then
        assertThatThrownBy(() -> indexController.service(request, HttpResponse.create()))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("index.html을 응답할 수 있다.")
    void requestTest() throws Exception {
        //when
        HttpResponse response = HttpResponse.create();
        indexController.service(request, response);

        //then
        final StaticResource staticResource = StaticResource.from("/index.html");
        final byte[] content = staticResource.getContent();
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + content.length + " ",
                "",
                new String(content, StandardCharsets.UTF_8));
        assertThat(expected).isEqualTo(response.toString());
    }
}
