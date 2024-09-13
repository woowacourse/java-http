package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.apache.catalina.session.Session;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController controller;

    public LoginControllerTest() {
        this.controller = LoginController.getInstance();
    }

    @DisplayName("login으로 GET 요청을 보내면 login.html을 200으로 응답한다")
    @Test
    void getLogin() {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setPath("/login");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getResourceName())
                        .isEqualTo("/login.html"),
                () -> assertThat(response.getHttpStatus())
                        .isEqualTo(HttpStatus.OK)
        );
    }

    @DisplayName("로그인에 성공하면 302와 함께 index.html로 리다이렉트한다")
    @Test
    void successLogin() {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.POST);
        request.setPath("/login");
        request.setBody("account=gugu&password=password");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getLocation())
                        .isEqualTo("/index.html"),
                () -> assertThat(response.getHttpStatus())
                        .isEqualTo(HttpStatus.FOUND)
        );
    }

    @DisplayName("로그인에 실패하면 302와 함께 401.html로 리다이렉트한다")
    @Test
    void failLogin() {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.POST);
        request.setPath("/login");
        request.setBody("account=gwigwi&password=password");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getLocation())
                        .isEqualTo("/401.html"),
                () -> assertThat(response.getHttpStatus())
                        .isEqualTo(HttpStatus.FOUND)
        );
    }

    @DisplayName("로그인에 성공하면 Session에 user 정보를 저장한다")
    @Test
    void addUserToSession() {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.POST);
        request.setPath("/login");
        request.setBody("account=gugu&password=password");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        Session session = request.getSession();
        assertThat(session.getAttribute("user"))
                .isNotNull();
    }

    @DisplayName("로그인 상태에서 login에 GET 요청을 보낼 시 index.html로 리다이렉트한다")
    @Test
    void redirectWhenLogin() {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.POST);
        request.setPath("/login");
        request.setBody("account=gugu&password=password");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);
        request.setMethod(HttpMethod.GET);
        response = new HttpResponse();
        controller.service(request, response);

        // then
        assertThat(response.getLocation())
                .isEqualTo("/index.html");
    }
}
