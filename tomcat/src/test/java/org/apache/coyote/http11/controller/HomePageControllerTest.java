package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomePageControllerTest {

    private final Controller homePageController = HomePageController.getInstance();

    @DisplayName("GET / 요청이 오면 Hello world!를 반환한다.")
    @Test
    void process() throws Exception {
        // given
        String httpRequestMessage = String.join("\r\n",
                "GET /favicon.ico HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        ByteArrayInputStream inputStream = new ByteArrayInputStream(httpRequestMessage.getBytes());
        HttpRequest request = new HttpRequest(inputStream);
        OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(outputStream);

        // when
        homePageController.service(request, response);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: 12 ",
                "",
                "Hello world!",
                "");
        assertThat(outputStream.toString()).isEqualTo(expected);
        inputStream.close();
        outputStream.close();
    }
}
