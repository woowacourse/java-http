package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeaders;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class HomeControllerTest {

    private HomeController homeController;

    @BeforeEach
    void setUp() {
        homeController = new HomeController();
    }

    @Test
    void doGetTest_whenRequestIsValid_shouldSendHelloWorld() throws Exception {
        HttpRequestStartLine startLine = new HttpRequestStartLine("GET", "/", "HTTP/1.1");
        HttpRequestHeaders headers = new HttpRequestHeaders();
        HttpRequestBody body = new HttpRequestBody();
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        homeController.doGet(request, response);

        assertThat(response.getBody()).isEqualTo("Hello world!");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getStatusCode());
        assertThat(response.getHeaders().get("Content-Type")).isEqualTo("text/html;charset=utf-8");
    }

    @Test
    void doPostTest_whenPostRequest_shouldThrowUncheckedServletException() {
        HttpRequestStartLine startLine = new HttpRequestStartLine("POST", "/", "HTTP/1.1");
        HttpRequestHeaders headers = new HttpRequestHeaders();
        HttpRequestBody body = new HttpRequestBody();
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        assertThatThrownBy(() -> homeController.doPost(request, response))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("지원하지 않는 메서드입니다.");
    }
}
