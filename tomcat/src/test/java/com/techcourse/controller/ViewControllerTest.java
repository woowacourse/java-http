package com.techcourse.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.apache.coyote.http11.MimeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ViewControllerTest {
    private ViewController viewController;

    @BeforeEach
    void setUp() {
        viewController = ViewController.getInstance();
    }

    @DisplayName("요청에 따른 페이지를 반환한다.")
    @Test
    void viewPage() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));

        // when
        HttpResponse response = viewController.handle(httpRequest);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 " + HttpStatus.OK.getMessage() + " \r\n" +
                "Content-Length: " + responseBody.length() + " \r\n" +
                "Content-Type: " + MimeType.HTML.getType() + " \r\n" +
                "\r\n" +
                responseBody;

        assertThat(response.toString()).isEqualTo(expected);
    }

    @DisplayName("/는 hello.html 페이지를 반환한다.")
    @Test
    void viewHelloPage() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET / HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));

        // when
        HttpResponse response = viewController.handle(httpRequest);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/hello.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 " + HttpStatus.OK.getMessage() + " \r\n" +
                "Content-Length: " + responseBody.length() + " \r\n" +
                "Content-Type: " + MimeType.HTML.getType() + " \r\n" +
                "\r\n" +
                responseBody;

        assertThat(response.toString()).isEqualTo(expected);
    }

    @DisplayName("요청에 따른 페이지를 반환한다.")
    @Test
    void viewPageWithoutExtension() throws IOException {
        // given
        String request = String.join("\r\n",
                "GET /index HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ");
        HttpRequest httpRequest = new HttpRequest(new BufferedReader(new StringReader(request)));

        // when
        HttpResponse response = viewController.handle(httpRequest);

        //then
        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        String responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));
        String expected = "HTTP/1.1 " + HttpStatus.OK.getMessage() + " \r\n" +
                "Content-Length: " + responseBody.length() + " \r\n" +
                "Content-Type: " + MimeType.HTML.getType() + " \r\n" +
                "\r\n" +
                responseBody;

        assertThat(response.toString()).isEqualTo(expected);
    }
}
