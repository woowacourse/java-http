package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldControllerTest {

    @Test
    void respondsWithHelloWorldForGetRequest() throws Exception {
        HttpRequest request = HttpRequest.parse(new BufferedReader(new StringReader(
                "GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n"
        )));
        HttpResponse response = new HttpResponse();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        new HelloWorldController().service(request, response);
        response.writeTo(outputStream);

        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/html;charset=utf-8\r\n"
                        + "Content-Length: 12\r\n"
                        + "\r\n"
                        + "Hello world!"
        );
    }

    @Test
    void respondsWithMethodNotAllowedForPostRequest() throws Exception {
        HttpRequest request = HttpRequest.parse(new BufferedReader(new StringReader(
                "POST / HTTP/1.1\r\nContent-Length: 0\r\n\r\n"
        )));
        HttpResponse response = new HttpResponse();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        new HelloWorldController().service(request, response);
        response.writeTo(outputStream);

        assertThat(outputStream.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 405 Method Not Allowed\r\n"
                        + "Content-Type: text/plain;charset=utf-8\r\n"
                        + "Content-Length: 18\r\n"
                        + "\r\n"
                        + "Method Not Allowed"
        );
    }
}
