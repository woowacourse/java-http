package com.techcourse.controller;

import jakarta.http.HttpRequest;
import jakarta.http.HttpResponse;
import jakarta.http.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class HelloControllerTest {

    @Test
    @DisplayName("GET 요청을 처리할 수 있다.")
    void doGet() throws Exception {
        HelloController helloController = new HelloController();
        HttpResponse response = HttpResponse.createHttpResponse(HttpVersion.HTTP_1_1);

        helloController.doGet(Mockito.mock(HttpRequest.class), response);

        assertThat(response.getBody()).isEqualTo("Hello world!");
    }
}
