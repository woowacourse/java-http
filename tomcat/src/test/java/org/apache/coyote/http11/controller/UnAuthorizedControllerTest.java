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

class UnAuthorizedControllerTest {

    private final Controller unAuthorizedController = new UnAuthorizedController();
    private HttpRequest request;

    @BeforeEach
    void setUp() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "GET /401.html HTTP/1.1 ",
                "Content-Type: text/html",
                "",
                "");
        final StringReader stringReader = new StringReader(rawRequest);
        request = HttpRequest.from(new BufferedReader(stringReader));
    }

    @Test
    @DisplayName("Reqeust를 해당 컨트롤러가 처리할 수 있다.")
    void handleTest() {

        //when, then
        assertThat(unAuthorizedController.canHandle(request)).isTrue();
    }

    @Test
    @DisplayName("지원하지 않는 HttpMethod는 해당 컨트롤러가 처리할 수 없다.")
    void handleExceptionTest() throws IOException {
        // given
        final String rawRequest = String.join("\r\n",
                "POST /401.html HTTP/1.1 ",
                "Content-Type: text/html",
                "",
                "");
        final StringReader stringReader = new StringReader(rawRequest);
        request = HttpRequest.from(new BufferedReader(stringReader));

        //when, then
        assertThatThrownBy(() -> unAuthorizedController.service(request))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    @DisplayName("401.html을 응답할 수 있다.")
    void requestTest() throws Exception {
        //when
        final HttpResponse response = unAuthorizedController.service(request);

        //then
        final StaticResource staticResource = StaticResource.from("/401.html");
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
