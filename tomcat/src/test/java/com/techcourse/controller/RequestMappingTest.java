package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    @Test
    @DisplayName("요청 경로가 / 이면 홈 컨트롤러를 반환한다.")
    void getHomeController() {
        // given
        RequestMapping mapper = new RequestMapping();
        HttpRequest request = HttpRequest.parse(List.of("GET / HTTP/1.1"));

        // when
        Controller controller = mapper.getController(request);

        // then
        assertThat(controller).isInstanceOf(HomeController.class);
    }

    @Test
    @DisplayName("요청 경로가 /login 이면 로그인 컨트롤러를 반환한다.")
    void getLoginController() {
        // given
        RequestMapping mapper = new RequestMapping();
        HttpRequest request = HttpRequest.parse(List.of("GET /login HTTP/1.1"));

        // when
        Controller controller = mapper.getController(request);

        // then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    @DisplayName("요청 경로가 /register 이면 회원가입 컨트롤러를 반환한다.")
    void getRegisterController() {
        // given
        RequestMapping mapper = new RequestMapping();
        HttpRequest request = HttpRequest.parse(List.of("GET /register HTTP/1.1"));

        // when
        Controller controller = mapper.getController(request);

        // then
        assertThat(controller).isInstanceOf(RegisterController.class);
    }


    @Test
    @DisplayName("예약되지 않은 요청 경로에 대해 리소스 컨트롤러를 반환한다.")
    void getResourceController() {
        // given
        RequestMapping mapper = new RequestMapping();
        HttpRequest request = HttpRequest.parse(List.of("GET /index.html HTTP/1.1"));

        // when
        Controller controller = mapper.getController(request);

        // then
        assertThat(controller).isInstanceOf(ResourceController.class);
    }
}
