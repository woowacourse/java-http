package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.service.UserService;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.component.HttpHeaders;
import org.apache.coyote.http11.component.HttpStatus;
import org.apache.coyote.http11.fixture.HttpRequestFixture;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setup() {
        UserService userService = new UserService();
        loginController = new LoginController(userService);
    }

    @DisplayName("GET 로그인 요청을 성공하면 200 OK를 반환한다.")
    @Test
    void doGet() {
        //given
        HttpRequest request = HttpRequestFixture.getGetRequest("/login");
        HttpResponse response = new HttpResponse(request);

        //when
        loginController.doGet(request, response);

        //then
        assertThat(response.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.OK);
    }

    @DisplayName("GET 로그인 요청시 세션이 존재하면 302 FOUND와 Location 헤더에 기본 경로를 포함한다.")
    @Test
    void doGetWithSession() {
        //given
        UUID uuid = UUID.randomUUID();
        SessionManager.getInstance().add(new Session(uuid.toString()));
        HttpRequest request = HttpRequestFixture.getGetRequestWithSession("/login", uuid.toString());
        HttpResponse response = new HttpResponse(request);

        //when
        loginController.doGet(request, response);

        //then
        assertAll(
                () -> assertThat(response.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getHeaders()).containsEntry(HttpHeaders.LOCATION, "/index.html")
        );
    }

    @DisplayName("GET 로그인 요청시 예외를 잡으면 302 FOUND와 Location 헤더에 401 경로를 포함한다.")
    @Test
    void doGetThrowsException() {
        //given
        UUID uuid = UUID.randomUUID();
        SessionManager.getInstance().add(new Session(uuid.toString()));
        HttpRequest request = HttpRequestFixture.getGetRequest("/login?account=daon");
        HttpResponse response = new HttpResponse(request);

        //when
        loginController.doGet(request, response);

        //then
        assertAll(
                () -> assertThat(response.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getHeaders()).containsEntry(HttpHeaders.LOCATION, "/401.html")
        );
    }

    @DisplayName("로그인에 성공하면 302 FOUND와 Location 헤더에 메인페이지 경로를 포함한다.")
    @Test
    void doPost() {
        //given
        Map<String, String> params = Map.of(
                "account", "gugu",
                "password", "password"
        );
        HttpRequest request = HttpRequestFixture.getPostRequest("/login", params);
        HttpResponse response = new HttpResponse(request);

        //when
        loginController.doPost(request, response);

        //then
        assertAll(
                () -> assertThat(response.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getHeaders()).containsEntry(HttpHeaders.LOCATION, "/index.html")
        );
    }

    @DisplayName("로그인 과정에서 에외가 발생하면 302 FOUND와 Location 헤더에 메인페이지 경로를 포함한다.")
    @Test
    void doPostThrowException() {
        //given
        Map<String, String> params = Map.of(
                "account", "",
                "password", "password"
        );
        HttpRequest request = HttpRequestFixture.getPostRequest("/login", params);
        HttpResponse response = new HttpResponse(request);

        // when
        loginController.doPost(request, response);

        //then
        assertAll(
                () -> assertThat(response.getStatusLine().getHttpStatus()).isEqualTo(HttpStatus.FOUND),
                () -> assertThat(response.getHeaders()).containsEntry(HttpHeaders.LOCATION, "/401.html")
        );
    }
}
