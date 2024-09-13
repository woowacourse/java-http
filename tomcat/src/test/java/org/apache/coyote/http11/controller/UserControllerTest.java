package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.header.HttpRequestHeader;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseBody;
import org.apache.coyote.http11.response.ViewResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UserControllerTest {

    private UserController userController = UserController.getInstance();

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.clear();
    }

    @DisplayName("url에 register가 포함되어 있으면 userController가 다룰 수 있는 요청임을 판단한다")
    @Test
    void canHandle() {
        String userControllerRequestUri = "register";
        String otherUri = "";

        assertAll(
                ()-> assertThat(userController.canHandle(userControllerRequestUri)).isTrue(),
                ()-> assertThat(userController.canHandle(otherUri)).isFalse()
        );
    }

    @DisplayName("post register 요청이 들어오면 새로운 멤버가 추가된다")
    @Test
    void doPost() {
        HttpRequest postRegisterRequest = new HttpRequest(
                new RequestLine("POST /register HTTP/1.1"),
                new HttpRequestHeader(),
                new RequestBody("account=test&password=password&email=hkkang%40woowahan.com")
        );

        userController.doPost(postRegisterRequest, new HttpResponse());

        assertThat(InMemoryUserRepository.findByAccount("test")).isPresent();
    }

    @DisplayName("get register 요청이 들어오면 register.html을 반환한다")
    @Test
    void doGet() {
        HttpResponse response = new HttpResponse();
        HttpRequest getRequest = new HttpRequest(
                new RequestLine("GET /register HTTP/1.1"),
                new HttpRequestHeader()
        );

        userController.doGet(getRequest, response);

        ResponseBody responseBody = response.getResponseBody();
        String expected = new ViewResolver().findResponseFile("/register.html");
        assertThat(responseBody.getValue()).isEqualTo(expected);
    }
}
