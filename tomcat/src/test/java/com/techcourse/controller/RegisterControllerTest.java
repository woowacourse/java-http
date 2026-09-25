package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    private final RegisterController controller = new RegisterController();

    @Test
    void GET_요청에는_회원가입_페이지를_응답한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest("GET /register HTTP/1.1\r\n\r\n");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        String message = response.toResponse();
        assertThat(message).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(message).contains("Content-Type: text/html;charset=utf-8\r\n");
        assertThat(message).contains("<title>회원가입</title>");
    }

    @Test
    void POST_요청에는_사용자를_저장하고_로그인_상태로_리다이렉트한다() throws Exception {
        // given
        String account = "user-" + UUID.randomUUID();
        String body = "account=" + account + "&email=user%40example.com&password=password";
        HttpRequest request = new HttpRequest(String.join("\r\n",
                "POST /register HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                body
        ));
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        String message = response.toResponse();
        assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
        assertThat(message).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(message).contains("Location: /index.html\r\n");
        assertThat(message).containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]{36}; Path=/\\r\\n");
    }
}
