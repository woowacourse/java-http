package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FrontControllerTest {
    private FrontController frontController;

    @BeforeEach
    void setUp() {
        frontController = new FrontController();
    }

    @DisplayName("올바른 리소스에 대해 200 응답을 반환한다.")
    @Test
    void index() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));


        // when
        String result = frontController.handle(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        var expected = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 5564 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("존재하지 않는 리소스에 접근하면 404 응답을 반환한다.")
    @Test
    void wrongURL() throws IOException {
        // given
        final String request = String.join("\r\n",
                "GET /unknown.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));

        // when
        String result = frontController.handle(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/404.html");
        var expected = "HTTP/1.1 404 NOT FOUND \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 2426 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("잘못된 메서드로 요청하면 405 응답을 반환한다.")
    @Test
    void wrongMethod() throws IOException {
        // given
        final String request = String.join("\r\n",
                "DELETE /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));

        // when
        String result = frontController.handle(httpRequest);

        // then
        final URL resource = getClass().getClassLoader().getResource("static/405.html");
        var expected = "HTTP/1.1 405 METHOD NOT ALLOWED \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: 2190 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        assertThat(result).isEqualTo(expected);
    }
}
