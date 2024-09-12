package com.techcourse.controller;


import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.request.Http11Request;
import org.apache.coyote.http11.request.Http11RequestBody;
import org.apache.coyote.http11.request.Http11RequestHeaders;
import org.apache.coyote.http11.request.Http11RequestLine;
import org.apache.coyote.http11.response.Http11Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginRequestControllerTest {

    private RequestHandler handler = new LoginRequestController();

    @DisplayName("로그인을 하면 /index.html로 리다이렉트 한다.")
    @Test
    void handle1() throws Exception {
        String body = "account=gugu&password=password";
        Http11RequestLine requestLine = new Http11RequestLine("POST /login HTTP/1.1");
        Http11RequestHeaders headers = new Http11RequestHeaders(
                List.of("Content-Length: " + body.getBytes().length));
        Http11RequestBody requestBody = new Http11RequestBody(body);
        HttpRequest request = new Http11Request(requestLine, headers, requestBody);
        HttpResponse response = new Http11Response();
        handler.handle(request, response);
        String actual = response.getResponseMessage();
        String expectedResponseLine = "HTTP/1.1 302 Found";
        String expectedLocation = "/index.html";
        assertThat(actual).contains(expectedResponseLine);
        assertThat(actual).contains(expectedLocation);
    }

    @DisplayName("쿠키에 세션 id가 존재하면, /login GET 요청 시 /index.html로 리다이렉트 된다.")
    @Test
    void handle2() throws Exception {
        Http11RequestLine requestLine = new Http11RequestLine("GET /login HTTP/1.1");
        Http11RequestHeaders headers = new Http11RequestHeaders(
                List.of("Cookie: JSESSIONID=123")
        );
        Http11RequestBody requestBody = new Http11RequestBody("");
        HttpRequest request = new Http11Request(requestLine, headers, requestBody);
        HttpResponse response = new Http11Response();

        handler.handle(request, response);

        String actual = response.getResponseMessage();
        String expectedResponseLine = "HTTP/1.1 302 Found";
        String expectedLocation = "/index.html";

        assertThat(actual).contains(expectedResponseLine);
        assertThat(actual).contains(expectedLocation);
    }
}
