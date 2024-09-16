package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StaticControllerTest {

    @Test
    @DisplayName("정적 파일 요청을 처리한다.")
    void controlStaticFile() {
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("GET /index.html HTTP/1.1"),
                new HttpRequestHeader(
                        List.of("Host: localhost:8080", "Connection: keep-alive", "Accept: */*")),
                new HttpRequestBody("account=gugu&password=password")
        );
        HttpResponse response = HttpResponse.defaultResponse();
        Controller controller = new StaticController();
        controller.service(request, response);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }

    @Test
    @DisplayName("css 파일 요청을 처리한다.")
    void controlCSSFile() {
        HttpRequest request = new HttpRequest(
                new HttpRequestLine("GET /css/styles.css HTTP/1.1"),
                new HttpRequestHeader(
                        List.of("Host: localhost:8080", "Accept: text/css,*/*;q=0.1", "Connection: keep-alive")),
                new HttpRequestBody("")
        );
        HttpResponse response = HttpResponse.defaultResponse();
        Controller controller = new StaticController();
        controller.service(request, response);

        assertThat(response.getStatusLine().getStatusCode()).isEqualTo(200);
    }

}
