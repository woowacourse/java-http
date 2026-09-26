package com.techcourse.controller;

import org.apache.catalina.controller.Controller;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.http11.HttpRequest;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static support.HttpRequestFixtures.httpRequest;

class RequestMappingTest {

    private final RequestMapping requestMapping = new RequestMapping(
            Map.of(
                    "/", new RootController(),
                    "/login", new LoginController(),
                    "/register", new RegisterController()
            ),
            new StaticResourceController()
    );

    @Test
    void 루트_경로를_RootController에_매핑한다() {
        HttpRequest request = httpRequest("GET / HTTP/1.1\r\n\r\n");

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(RootController.class);
    }

    @Test
    void 로그인_경로를_LoginController에_매핑한다() {
        HttpRequest request = httpRequest("GET /login HTTP/1.1\r\n\r\n");

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @Test
    void 회원가입_경로를_RegisterController에_매핑한다() {
        HttpRequest request = httpRequest("GET /register HTTP/1.1\r\n\r\n");

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(RegisterController.class);
    }

    @Test
    void 등록되지_않은_경로를_StaticResourceController에_매핑한다() {
        HttpRequest request = httpRequest("GET /index.html HTTP/1.1\r\n\r\n");

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(StaticResourceController.class);
    }

    @Test
    void 쿼리_문자열을_제외한_경로로_매핑한다() {
        HttpRequest request = httpRequest("GET /login?from=index HTTP/1.1\r\n\r\n");

        Controller controller = requestMapping.getController(request);

        assertThat(controller).isInstanceOf(LoginController.class);
    }
}
