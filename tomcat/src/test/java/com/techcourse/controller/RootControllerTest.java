package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RootControllerTest {

    private RootController rootController;
    private HttpResponse httpResponse;

    @BeforeEach
    void setUp() {
        rootController = new RootController();
        httpResponse = new HttpResponse();
    }

    @Test
    void doGet() throws IOException {
        String request = "GET / HTTP/1.1 \r\n" +
                "Host: localhost:8080 \r\n\r\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(request));
        HttpRequest httpRequest = new HttpRequest(bufferedReader);

        rootController.doGet(httpRequest, httpResponse);
        String actual = new String(httpResponse.buildResponse());
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Length: 12 ",
                "Content-Type: text/html;charset=utf-8 ",
                "",
                "Hello world!");

        assertThat(actual).isEqualTo(expected);
    }
}
