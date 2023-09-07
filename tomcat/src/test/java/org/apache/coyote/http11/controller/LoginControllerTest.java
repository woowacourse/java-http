package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.exception.HttpRequestException;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.security.Session;
import org.apache.coyote.http11.security.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("GET /login 요청 시 SessionID에 해당하는 세션이 존재하지 않으면 예외가 발생한다")
    void throwsWhenNotMatchSessionId() throws Exception {
        // given
        String startLine = "GET /login HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "Content-Type: text/html",
                "Cookie: JSESSIONID=notMatchSession",
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        LoginController loginController = new LoginController();

        // when & then
        assertThatThrownBy(() -> loginController.service(request))
                .isInstanceOf(HttpRequestException.NotMatchSession.class)
                .hasMessage("SessionId와 일치하는 세션이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("GET /login 요청 시 SessionID에 해당하는 세션이 존재하면 index.html로 리다이렉트한다.")
    void redirectWhenMatchSessionId() throws Exception {
        // given
        String sessionId = "matchSession";
        Session session = new Session(sessionId);
        SessionManager sessionManager = new SessionManager();
        sessionManager.add(session);

        String startLine = "GET /login HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "Content-Type: text/html",
                "Cookie: JSESSIONID=" + sessionId,
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        LoginController loginController = new LoginController();

        final URL resource = getClass().getClassLoader().getResource("static/index.html");
        final String expectedResponse = "HTTP/1.1 302 Found \r\n" +
                "Content-Type: text/html \r\n" +
                "Location: /index.html \r\n" +
                "\r\n" +
                "";

        // when
        HttpResponse response = loginController.service(request);
        String actualResponse = new String(response.convertToBytes());

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }

    @Test
    @DisplayName("GET /login 요청 시 SessionID가 존재하지 않으면 로그인 페이지를 보여준다.")
    void getLoginPageWhenNotExistSessionId() throws Exception {
        // given

        String startLine = "GET /login HTTP/1.1 ";
        String rawRequest = String.join("\r\n",
                "Content-Type: text/html",
                "",
                ""
        );
        StringReader stringReader = new StringReader(rawRequest);
        BufferedReader br = new BufferedReader(stringReader);
        HttpRequest request = HttpRequest.from(br, startLine);

        LoginController loginController = new LoginController();

        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        final String expectedResponse = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html \r\n" +
                "Content-Length: 3537 \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        // when
        HttpResponse response = loginController.service(request);
        String actualResponse = new String(response.convertToBytes());

        // then
        assertThat(actualResponse).isEqualTo(expectedResponse);
    }
}
