package com.techcourse.controller;

import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class StaticResourceControllerTest {

    @DisplayName("정적 리소스 페이지로 Get 요청 시 200 반환")
    @ParameterizedTest
    @CsvSource(value = {"/css/styles.css", "/js/scripts.js"})
    void staticResource_css(String path) throws Exception {
        //given
        StaticResourceController staticResourceController = new StaticResourceController();
        HttpRequestLine requestLine = HttpRequestLine.from("GET " + path + " HTTP/1.1");
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost:8080");
        headers.put("Connection", "keep-alive");
        HttpHeaders httpHeaders = HttpHeaders.from(headers);
        HttpRequest request = new HttpRequest(requestLine, httpHeaders, null);
        HttpResponse response = new HttpResponse();

        //when
        staticResourceController.doGet(request, response);

        //then
        assertThat(response.getHttpStatus()).isEqualTo(200);
    }
}