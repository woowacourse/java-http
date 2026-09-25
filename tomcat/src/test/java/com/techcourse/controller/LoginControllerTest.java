package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private final LoginController controller = new LoginController();

    @Test
    void 로그인하지_않은_GET_요청에는_로그인_페이지를_응답한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest("GET /login HTTP/1.1\r\n\r\n");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        String message = response.toResponse();
        assertThat(message).startsWith("HTTP/1.1 200 OK\r\n");
        assertThat(message).contains("Content-Type: text/html;charset=utf-8\r\n");
        assertThat(message).contains("<title>로그인</title>");
    }

    @Test
    void 로그인한_GET_요청은_인덱스_페이지로_리다이렉트한다() throws Exception {
        // given
        String sessionId = UUID.randomUUID().toString();
        Session session = new Session(sessionId);
        session.setAttribute("loginUser", InMemoryUserRepository.findByAccount("gugu").orElseThrow());
        SessionManager.add(session);

        HttpRequest request = new HttpRequest(String.join("\r\n",
                "GET /login HTTP/1.1",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""
        ));
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        String message = response.toResponse();
        assertThat(message).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(message).contains("Location: /index.html\r\n");
    }

    @Test
    void 로그인에_성공하면_세션_쿠키와_함께_인덱스_페이지로_리다이렉트한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest(String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=password"
        ));
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        String message = response.toResponse();
        assertThat(message).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(message).contains("Location: /index.html\r\n");
        assertThat(message).containsPattern("Set-Cookie: JSESSIONID=[0-9a-f-]{36}; Path=/\\r\\n");
    }

    @Test
    void 로그인에_실패하면_401_페이지로_리다이렉트한다() throws Exception {
        // given
        HttpRequest request = new HttpRequest(String.join("\r\n",
                "POST /login HTTP/1.1",
                "Content-Type: application/x-www-form-urlencoded",
                "",
                "account=gugu&password=wrong"
        ));
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        String message = response.toResponse();
        assertThat(message).startsWith("HTTP/1.1 302 Found\r\n");
        assertThat(message).contains("Location: /401.html\r\n");
    }
}
