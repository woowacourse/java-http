package com.techcourse.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import org.apache.http.header.HttpHeader;
import org.apache.http.header.HttpHeaderName;
import org.apache.http.header.HttpHeaders;
import org.apache.http.request.HttpRequest;
import org.apache.http.request.RequestLine;
import org.apache.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @Test
    @DisplayName("GET 요청 처리: 회원가입 페이지 반환")
    void handle_GetRequest() throws Exception {
        // given
        final RequestLine requestLine = new RequestLine("GET", "/register.html", "HTTP/1.1");
        final HttpRequest request = new HttpRequest(requestLine);
        final HttpResponse actual = HttpResponse.builder().okBuild();

        // when
        RegisterController.getInstance().service(request, actual);

        final HttpResponse expected = HttpResponse.builder()
                .foundBuild("/register.html");
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("POST 요청 처리: 유효한 회원가입 정보로 회원가입 성공")
    void handle_PostRequest_WithValidRegistration() throws Exception {
        // given
        final RequestLine requestLine = new RequestLine("POST", "/index.html", "HTTP/1.1");
        final HttpHeaders headers = new HttpHeaders(new HttpHeader(HttpHeaderName.CONTENT_TYPE, "application/x-www-form-urlencoded"));
        final HttpRequest request = new HttpRequest(requestLine, headers, "account=newuser&email=newuser@example.com&password=password123");
        final HttpResponse actual = HttpResponse.builder().okBuild();

        // when
        RegisterController.getInstance().service(request, actual);

        // then
        final HttpResponse expected = HttpResponse.builder().foundBuild("/index.html");
        assertThat(actual).isEqualTo(expected);
    }
}
