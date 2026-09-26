package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.service.UserService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private final LoginController controller = new LoginController(new UserService());

    @Test
    void 로그인에_성공하면_세션에_유저를_저장하고_index로_리다이렉트한다() throws Exception {
        HttpRequest request = postLogin("account=gugu&password=password");
        HttpResponse response = HttpResponse.empty();

        controller.service(request, response);

        assertThat(output(response))
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /index.html ");
        assertThat(LoginSession.of(request).isLoggedIn()).isTrue();
    }

    @Test
    void 로그인에_실패하면_401로_리다이렉트한다() throws Exception {
        HttpRequest request = postLogin("account=gugu&password=wrong");
        HttpResponse response = HttpResponse.empty();

        controller.service(request, response);

        assertThat(output(response))
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /401.html ");
        assertThat(LoginSession.of(request).isLoggedIn()).isFalse();
    }

    @Test
    void 로그인한_상태에서_로그인_페이지에_접근하면_index로_리다이렉트한다() throws Exception {
        HttpRequest loginRequest = postLogin("account=gugu&password=password");
        controller.service(loginRequest, HttpResponse.empty());

        HttpRequest pageRequest = HttpRequest.from(List.of("GET /login.html HTTP/1.1"), "")
                .withSessionId(loginRequest.getSessionId());
        HttpResponse response = HttpResponse.empty();

        controller.service(pageRequest, response);

        assertThat(output(response))
                .startsWith("HTTP/1.1 302 Found ")
                .contains("Location: /index.html ");
    }

    private HttpRequest postLogin(String body) throws IOException {
        Session session = SessionManager.getInstance().findOrCreate(null);
        return HttpRequest.from(List.of("POST /login HTTP/1.1"), body)
                .withSessionId(session.getId());
    }

    private String output(HttpResponse response) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        response.writeTo(outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }
}
