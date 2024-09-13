package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    @DisplayName("회원가입 페이지로 Get 요청 시 200 반환")
    @Test
    void register_page() throws Exception {
        //given
        RegisterController registerController = new RegisterController();
        HttpRequestLine requestLine = HttpRequestLine.from("GET /register HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        HttpHeaders httpHeaders = HttpHeaders.from(headers);
        HttpRequest request = new HttpRequest(requestLine, httpHeaders, null);
        HttpResponse response = new HttpResponse();

        //when
        registerController.doGet(request, response);

        //then
        assertThat(response.getHttpStatus()).isEqualTo(200);
    }

    @DisplayName("회원가입 POST 요청 성공 시 302 반환")
    @Test
    void register_success() throws Exception {
        //given
        RegisterController registerController = new RegisterController();
        HttpRequestLine requestLine = HttpRequestLine.from("GET /register HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        HttpHeaders httpHeaders = HttpHeaders.from(headers);
        HttpRequestBody httpRequestBody = HttpRequestBody.from(
                "account=gugu&password=password&email=hkkang%40woowahan.com");
        HttpRequest request = new HttpRequest(requestLine, httpHeaders, httpRequestBody);
        HttpResponse response = new HttpResponse();

        //when
        registerController.doPost(request, response);

        //then
        assertThat(response.getHttpStatus()).isEqualTo(302);
    }
}