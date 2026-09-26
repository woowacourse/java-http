package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController controller = new LoginController();

    @Test
    void 로그인에_성공하면_세션에_유저를_저장하고_index로_리다이렉트한다() throws Exception {
        HttpRequest request = postLogin("account=gugu&password=password");
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader("Location")).isEqualTo("/index.html");
        assertThat(request.getSession().getAttribute("user")).isNotNull();
    }

    @Test
    void 로그인에_실패하면_401로_리다이렉트한다() throws Exception {
        HttpRequest request = postLogin("account=gugu&password=wrong");
        HttpResponse response = new HttpResponse();

        controller.service(request, response);

        assertThat(response.getStatus()).isEqualTo(HttpStatus.FOUND);
        assertThat(response.getHeader("Location")).isEqualTo("/401.html");
    }

    private HttpRequest postLogin(String body) throws IOException {
        String rawRequest = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Length: " + body.length(),
                "",
                body);
        return HttpRequest.from(new BufferedReader(new StringReader(rawRequest)));
    }
}
