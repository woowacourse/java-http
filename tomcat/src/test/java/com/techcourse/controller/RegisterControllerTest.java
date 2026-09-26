package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.RequestLine;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    void redirectsToLogin() throws IOException {
        // given
        String body = "account=register_test&password=password&email=test@example.com";

        // when
        String response = post(body);

        // then
        assertThat(response).startsWith("HTTP/1.1 302 Found\r\n")
                .contains("\r\nLocation: /login.html\r\n");
    }

    @Test
    void rejectsInvalidInput() throws IOException {
        // given
        String body = "account=register_test&password=password";

        // when
        String response = post(body);

        // then
        assertThat(response).startsWith("HTTP/1.1 302 Found\r\n")
                .contains("\r\nLocation: /register.html\r\n");
    }

    private String post(String body) throws IOException {
        HttpRequest request = new HttpRequest(
                RequestLine.from("POST /register HTTP/1.1"),
                new HttpHeaders(Map.of()),
                body
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        new RegisterController().service(request).write(output);

        return output.toString(StandardCharsets.UTF_8);
    }
}
