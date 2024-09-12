package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http11.exception.NotFoundException;
import org.apache.coyote.http11.exception.UnauthorizedException;
import org.apache.coyote.http11.httprequest.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.HttpRequestMaker;

class RequestMappingTest {

    @DisplayName("해당 path에 알맞은 Controller를 반환한다")
    @Test
    void getController() {
        final String login = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(login);

        RequestMapping requestMapping = new RequestMapping();
        Controller controller = requestMapping.getController(httpRequest);

        assertThat(controller)
                .isInstanceOf(LoginController.class);
    }

    @DisplayName("등록되지 않은 path일 경우 예외를 발생시킨다")
    @Test
    void notExistPath() {
        final String request = String.join("\r\n",
                "GET /lo HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(request);

        RequestMapping requestMapping = new RequestMapping();

        assertThatThrownBy(() -> requestMapping.getController(httpRequest))
                .isInstanceOf(NotFoundException.class);
    }

    @DisplayName("권한이 없는 페이지로 접근할 경우 예외를 발생시킨다")
    @Test
    void unauthorizedPath() {
        final String request = String.join("\r\n",
                "GET /500 HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "");
        HttpRequest httpRequest = HttpRequestMaker.makeHttpRequest(request);

        RequestMapping requestMapping = new RequestMapping();

        assertThatThrownBy(() -> requestMapping.getController(httpRequest))
                .isInstanceOf(UnauthorizedException.class);
    }
}
