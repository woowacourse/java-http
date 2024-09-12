package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    @Test
    @DisplayName("GET 요청 동작 확인")
    void doGet() throws IOException {
        HomeController homeController = new HomeController();
        String httpRequest = String.join("\r\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080"
                );
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequest));
        HttpRequest request = new HttpRequest(bufferedReader);
        HttpResponse httpResponse = new HttpResponse();

        homeController.doGet(request, httpResponse);

        String actual = "HTTP/1.1 200 OK \r\n"
                + "Content-Type: text/html;charset=utf-8 \r\n"
                + "Content-Length: 12 \r\n"
                + "\r\n"
                + "Hello world!";
        String response = httpResponse.getResponse();
        assertThat(actual).isEqualTo(response);
    }
}
