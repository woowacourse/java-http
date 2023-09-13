package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import nextstep.jwp.controller.HelloController;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HelloControllerTest {

    private final Controller controller = new HelloController();

    @Test
    @DisplayName("/ 로 GET 요청을 하면 Hello world! 와 200 OK가 반환된다.")
    void doGet() throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
            "GET / HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Accept: text/html",
            "",
            "");
        HttpRequest httpRequest;
        final HttpResponse httpResponse = HttpResponse.create();
        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // when
        controller.service(httpRequest, httpResponse);

        // then
        final String message = httpResponse.convertToMessage();
        final String body = "Hello world!";
        assertThat(message).contains(
            "HTTP/1.1", "200 OK",
            "Content-Type: text/html;charset=utf-8",
            "Content-Length: " + body.getBytes().length,
            body
        );
    }

    @Test
    @DisplayName("/ 에 POST 요청을 보내면 예외가 발생한다.")
    void doPost() throws Exception {
        // given
        final String httpRequestMessage = String.join("\r\n",
            "POST / HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "",
            "");
        HttpRequest httpRequest;
        final HttpResponse httpResponse = HttpResponse.create();
        try (final InputStream inputStream = new ByteArrayInputStream(
            httpRequestMessage.getBytes())) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            httpRequest = HttpRequest.from(reader);
        }

        // when, then
        assertThatThrownBy(() -> controller.service(httpRequest, httpResponse))
            .isInstanceOf(UnsupportedOperationException.class);
    }
}
