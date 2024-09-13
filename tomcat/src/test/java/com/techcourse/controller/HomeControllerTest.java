package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.StringReader;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestTestSupport;

class HomeControllerTest {

    private final HomeController homeController = new HomeController();

    @Test
    @DisplayName("/ 경로로 GET 요청하면 Hello world! 를 반환한다.")
    void get() throws Exception {
        HttpRequest request = HttpRequestTestSupport.homeGet();
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();

        homeController.service(request, builder);
        HttpResponse response = builder.build();

        assertEquals(response.getResponseBody(), "Hello world!");
    }

    @Test
    @DisplayName("/ 경로로 POST 요청하면 예외를 반환한다.")
    void post() throws Exception {
        String requestString = String.join("\r\n",
                "POST / HTTP/1.1",
                "");
        BufferedReader reader = new BufferedReader(new StringReader(requestString));

        HttpRequest request = HttpRequest.from(reader);
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();

        assertThatThrownBy(() -> homeController.service(request, builder))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
