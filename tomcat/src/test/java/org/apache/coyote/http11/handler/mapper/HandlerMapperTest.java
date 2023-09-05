package org.apache.coyote.http11.handler.mapper;

import org.apache.coyote.http11.handler.controller.base.Controller;
import org.apache.coyote.http11.handler.controller.login.LoginController;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class HandlerControllerTest {

    @DisplayName("GET /Login 요청시 LoginController를 반환한다.")
    @Test
    void returns_login_controller_when_request_is_get_login() throws Exception {
        // given
        String lines = String.join("\r\n",
                "GET /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        HttpRequest httpRequest = parserHttpRequestFromLines(lines);

        // when
        Controller controller = HandlerMapper.getController(httpRequest);

        // then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    @DisplayName("POST /Login 요청시 LoginController를 반환한다.")
    @Test
    void returns_login_controller_when_request_is_post_login() throws Exception {
        // given
        String lines = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        HttpRequest httpRequest = parserHttpRequestFromLines(lines);

        // when
        Controller controller = HandlerMapper.getController(httpRequest);

        // then
        assertThat(controller).isInstanceOf(LoginController.class);
    }

    private HttpRequest parserHttpRequestFromLines(final String lines) throws IOException {
        StringReader stringReader = new StringReader(lines);
        BufferedReader reader = new BufferedReader(stringReader);
        return HttpRequest.from(reader);
    }
}
