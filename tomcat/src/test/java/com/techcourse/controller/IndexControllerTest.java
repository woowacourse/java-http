package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.apache.coyote.http11.message.parser.HttpRequestParser;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class IndexControllerTest {

    @DisplayName("GET / -> 200 Hello world")
    @Test
    void doGet() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "");

        HttpRequest httpRequest = HttpRequestParser.parse(new ByteArrayInputStream(request.getBytes()));
        HttpResponse httpResponse = new HttpResponse();

        // when
        new IndexController().doGet(httpRequest, httpResponse);

        // then
        assertThat(httpResponse.convertMessage()).contains(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html;charset=utf-8",
                "Content-Length: 12",
                "Hello world!");
    }
}
