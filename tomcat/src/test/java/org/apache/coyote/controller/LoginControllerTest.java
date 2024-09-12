package org.apache.coyote.controller;

import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.Method;
import org.apache.coyote.http11.RequestLine;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.Status;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.HashMap;

class LoginControllerTest {

    @DisplayName("사용자 인증정보가 존재하면 index.html로 리다이렉트한다.")
    @Test
    void redirect_whenSessionExist() throws IOException {
        // given
        LoginController loginController = new LoginController();

        String sessionId = "0ddf724d-daf9-4ef1-9f13-5e9103a2d0aa";
        Session session = new Session(sessionId);
        session.setAttribute("user", sessionId);
        SessionManager.add(session);

        RequestLine requestLine = new RequestLine(Method.GET, "/login", "HTTP/1.1");
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Cookie", "Idea-56f698fe=c5be6597-c8ed-4450-bd98-59a6db9c0a1d; JSESSIONID=" + "0ddf724d-daf9-4ef1-9f13-5e9103a2d0aa");
        HttpHeaders httpHeaders = new HttpHeaders(requestHeaders);
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders);

        // when
        HttpResponse httpResponse = loginController.doGet(httpRequest);

        // then
        Assertions.assertThat(httpResponse.getStatusMessage()).isEqualTo(Status.FOUND.getStatusMessage());
    }

    @DisplayName("사용자 인증정보가 존재하지 않으면 요청된 페이지를 반환한다.")
    @Test
    void doGet_whenSessionNotExist() throws IOException {
        // given
        LoginController loginController = new LoginController();

        RequestLine requestLine = new RequestLine(Method.GET, "/login", "HTTP/1.1");
        HashMap<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("Cookie", "Idea-56f698fe=c5be6597-c8ed-4450-bd98-59a6db9c0a1d;");
        HttpHeaders httpHeaders = new HttpHeaders(requestHeaders);
        HttpRequest httpRequest = new HttpRequest(requestLine, httpHeaders);

        // when
        HttpResponse httpResponse = loginController.doGet(httpRequest);

        // then
        Assertions.assertThat(httpResponse.getStatusMessage()).isEqualTo(Status.OK.getStatusMessage());
        Assertions.assertThat(httpResponse.getContentType()).startsWith("text/html");
    }
}
