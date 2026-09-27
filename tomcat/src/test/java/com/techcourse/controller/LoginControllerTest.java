package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.model.User;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.catalina.Session;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.RequestLine;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    void showsLoginPageForAnonymousUser() throws Exception {
        //given
        Session session = new Session("anonymous-session");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpRequest request = request("GET", "");

        //when
        new LoginController(session).service(request, new HttpResponse(output));

        //then
        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 200 OK\r\n")
                .contains("<title>로그인</title>");
    }

    @Test
    void storesUserInSessionAfterSuccessfulLogin() throws Exception {
        //given
        Session session = new Session("login-session");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        HttpRequest request = request("POST", "account=gugu&password=password");

        //when
        new LoginController(session).service(request, new HttpResponse(output));

        //then
        User user = (User) session.getAttribute("user");
        assertThat(user.getAccount()).isEqualTo("gugu");
        assertThat(output.toString(StandardCharsets.UTF_8))
                .startsWith("HTTP/1.1 302 Found\r\nLocation: /index.html\r\n");
    }

    private HttpRequest request(String method, String body) {
        return new HttpRequest(new RequestLine(method, "/login", "", "HTTP/1.1"), Map.of(), body);
    }
}
