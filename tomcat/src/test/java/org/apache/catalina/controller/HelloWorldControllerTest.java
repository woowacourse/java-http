package org.apache.catalina.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class HelloWorldControllerTest {

    @Test
    void respondsWithHelloWorldForGetRequest() throws Exception {
        HttpRequest request = HttpRequest.parse(new ByteArrayInputStream(
                "GET / HTTP/1.1\r\nHost: localhost:8080\r\n\r\n".getBytes(StandardCharsets.UTF_8)
        ));
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
        HttpRequest request = HttpRequest.parse(new ByteArrayInputStream(
                "POST / HTTP/1.1\r\nContent-Length: 0\r\n\r\n".getBytes(StandardCharsets.UTF_8)
        ));
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
