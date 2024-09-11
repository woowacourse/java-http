package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.StubSocket;

public class HttpRequestTest {

    @DisplayName("GET 요청을 받았다면 true를 반환한다.")
    @Test
    void checkGet() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        Socket socket = new StubSocket(httpRequest);
        HttpRequest request = HttpRequest.of(socket.getInputStream());

        assertTrue(request.isGet());
    }

    @DisplayName("GET 요청이 아니라면 false를 반환한다.")
    @Test
    void checkGetWithPost() throws IOException {
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 29",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        Socket socket = new StubSocket(httpRequest);
        HttpRequest request = HttpRequest.of(socket.getInputStream());

        assertFalse(request.isGet());
    }

    @DisplayName("POST 요청을 받았다면 true를 반환한다.")
    @Test
    void checkPost() throws IOException {
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 29",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        Socket socket = new StubSocket(httpRequest);
        HttpRequest request = HttpRequest.of(socket.getInputStream());

        assertTrue(request.isPost());
    }

    @DisplayName("POST 요청이 아니라면 false를 반환한다.")
    @Test
    void checkPostWithGet() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        Socket socket = new StubSocket(httpRequest);
        HttpRequest request = HttpRequest.of(socket.getInputStream());

        assertFalse(request.isPost());
    }

    @DisplayName("requestBody에 존재하면 해당 key의 값을, 존재하지 않으면 null을 반환한다.")
    @Test
    void findBodyValueByKey() throws IOException {
        String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 29",
                "Content-Type: application/x-www-form-urlencoded",
                "Accept: */*",
                "",
                "account=gugu&password=password");

        Socket socket = new StubSocket(httpRequest);
        HttpRequest request = HttpRequest.of(socket.getInputStream());

        assertAll(
                () -> assertThat(request.findBodyValueByKey("account")).isEqualTo("gugu"),
                () -> assertThat(request.findBodyValueByKey("email")).isNull()
        );
    }

    @DisplayName("응답 헤더 쿠키에 JSESSIONID가 없으면 true를 반환한다.")
    @Test
    void sessionNotExists() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        Socket socket = new StubSocket(httpRequest);
        HttpRequest request = HttpRequest.of(socket.getInputStream());

        assertTrue(request.sessionNotExists());
    }

    @DisplayName("응답 헤더 쿠키 JSESSIONID가 있으면 false를 반환한다.")
    @Test
    void sessionNotExistsWithJSESSIONID() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        Socket socket = new StubSocket(httpRequest);
        HttpRequest request = HttpRequest.of(socket.getInputStream());

        assertFalse(request.sessionNotExists());
    }

    @DisplayName("응답 헤더 쿠키의 JSESSIONID가 유효하면 세션을 찾아 반환한다.")
    @Test
    void getSession() throws IOException {
        Session session = new Session("656cef62-e3c4-40bc-a8df-94732920ed46");
        SessionManager.getInstance().add(session);
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        Socket socket = new StubSocket(httpRequest);
        HttpRequest request = HttpRequest.of(socket.getInputStream());

        assertThat(request.getSession(true)).isEqualTo(session);
    }

    @DisplayName("응답 헤더 쿠키의 JSESSIONID가 유효하지 않으면 null을 반환한다.")
    @Test
    void getSessionOutOfValidate() throws IOException {
        String httpRequest = String.join("\r\n",
                "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46 ",
                "",
                "");

        Socket socket = new StubSocket(httpRequest);
        HttpRequest request = HttpRequest.of(socket.getInputStream());

        assertThat(request.getSession(true)).isNull();
    }
}
