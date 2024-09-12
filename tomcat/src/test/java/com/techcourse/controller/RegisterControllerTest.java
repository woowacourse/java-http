package com.techcourse.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestTestSupport;

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @Test
    @DisplayName("/register 경로로 GET 요청하면 register 페이지를 반환한다.")
    void get() throws Exception {
        HttpRequest request = HttpRequestTestSupport.registerGet();
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();

        registerController.service(request, builder);
        HttpResponse response = builder.build();

        String expectedResponseBody = HttpRequestTestSupport.loadResourceContent("/register.html");
        assertEquals(expectedResponseBody, response.getResponseBody());
    }

    @Test
    @DisplayName("/register 경로로 POST 요청하면 User 가 생성되고, 알맞은 헤더를 응답한다.")
    void post() throws Exception {
        HttpRequest request = HttpRequestTestSupport.registerPost();
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();

        registerController.service(request, builder);
        HttpResponse response = builder.build();

        assertAll(
                () -> assertNotNull(InMemoryUserRepository.findByAccount("lily")),
                () -> assertEquals(response.getHeaders().get("Location"), "/index.html")
        );
    }
}
