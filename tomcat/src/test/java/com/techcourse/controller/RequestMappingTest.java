package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RequestMappingTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    void 루트_경로는_RootController를_반환한다() {
        final HttpRequest request = HttpRequest.from("GET / HTTP/1.1");

        final Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(RootController.class);
    }

    @Test
    void 로그인_경로는_LoginController를_반환한다() {
        final HttpRequest request = HttpRequest.from("GET /login HTTP/1.1");

        final Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    void 회원가입_경로는_RegisterController를_반환한다() {
        final HttpRequest request = HttpRequest.from("GET /register HTTP/1.1");

        final Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(RegisterController.class);
    }

    @Test
    void 등록되지_않은_경로는_StaticResourceController를_반환한다() {
        final HttpRequest request = HttpRequest.from("GET /css/styles.css HTTP/1.1");

        final Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(StaticResourceController.class);
    }
}
