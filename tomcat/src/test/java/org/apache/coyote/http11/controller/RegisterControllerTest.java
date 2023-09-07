package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    @DisplayName("GET 메소드에 해당하는 로직을 처리해서 HttpResponse를 반환한다.")
    void getService() throws Exception {
        // given
        String startLine = "GET /register HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "Content-Type: text/html",
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        RegisterController registerController = new RegisterController();

        final URL resource = getClass().getClassLoader().getResource("static/register.html");
        final String expectedResponse = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        HttpResponse response = registerController.service(request);
        String actualResponse = new String(response.convertToBytes());

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("POST 메소드 요청이 오면 예외를 반환한다.")
    void throwsWhenPostService() throws Exception {
        // given
        String startLine = "POST /register HTTP/1.1 ";
        String formData = "account=seongha&email=sh@gmail.com&password=sh";
        String rawRequest = String.join("\r\n",
                "Content-Type: application/x-www-form-urlencoded",
                "Content-Length: " + formData.getBytes().length,
                "",
                formData
        );

        System.out.println("rawRequest = " + rawRequest);
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);
        System.out.println("request.getMessageBody().getValue() = " + request.getMessageBody().getValue());

        RegisterController registerController = new RegisterController();
        final String expectedResponse = "HTTP/1.1 302 Found \r\n" +
                "Content-Type: text/html \r\n" +
                "Location: /index.html \r\n" +
                "\r\n";


        // when
        HttpResponse response = registerController.service(request);
        String actualResponse = new String(response.convertToBytes());

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

}
