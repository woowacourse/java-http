package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

class RequestMappingTest {

    private final RequestMapping requestMapping = new RequestMapping();

    @Test
    void 루트_경로는_HomeController가_처리한다() throws IOException {
        assertThat(requestMapping.getController(request("GET / HTTP/1.1")))
            .isInstanceOf(HomeController.class);
    }

    @Test
    void 로그인_경로는_쿼리스트링과_메서드에_상관없이_LoginController가_처리한다() throws IOException {
        assertThat(requestMapping.getController(request("GET /login HTTP/1.1")))
            .isInstanceOf(LoginController.class);
        assertThat(requestMapping.getController(request("GET /login?account=gugu&password=password HTTP/1.1")))
            .isInstanceOf(LoginController.class);
        assertThat(requestMapping.getController(request("POST /login HTTP/1.1")))
            .isInstanceOf(LoginController.class);
    }

    @Test
    void 회원가입_경로는_RegisterController가_처리한다() throws IOException {
        assertThat(requestMapping.getController(request("POST /register HTTP/1.1")))
            .isInstanceOf(RegisterController.class);
    }

    @Test
    void 매핑되지_않은_경로는_StaticResourceController가_처리한다() throws IOException {
        assertThat(requestMapping.getController(request("GET /index.html HTTP/1.1")))
            .isInstanceOf(StaticResourceController.class);
        assertThat(requestMapping.getController(request("GET /css/styles.css HTTP/1.1")))
            .isInstanceOf(StaticResourceController.class);
    }

    private HttpRequest request(String requestLine) throws IOException {
        return HttpRequest.from(new BufferedReader(new StringReader(requestLine + "\r\n\r\n")));
    }
}
