package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.controller.LoginController;
import java.io.BufferedReader;
import java.io.File;
import java.io.StringReader;
import java.net.URL;
import java.nio.file.Files;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    @Test
    @DisplayName("쿠키 없는 경우, GET 요청 동작 확인")
    void doGet() throws Exception {
        LoginController loginController = new LoginController(SessionManager.getSessionManager());

        final String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequest));
        HttpRequest request = new HttpRequest(bufferedReader);
        HttpResponse httpResponse = new HttpResponse();

        loginController.doGet(request, httpResponse);
        String responseBody = new HttpResponse().generateResponseBody("static/login.html");
        final URL resource = getClass().getClassLoader().getResource("static/login.html");
        String actual = "HTTP/1.1 200 OK \r\n" +
                "Content-Type: text/html;charset=utf-8 \r\n" +
                "Content-Length: " + responseBody.getBytes().length + " \r\n" +
                "\r\n" +
                new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

        String response = httpResponse.getResponse();
        assertThat(actual).isEqualTo(response);
    }

    @Test
    @DisplayName("쿠키 존재하는 경우 GET 요청 동작 확인")
    void doGetWithCookie() throws Exception {
        SessionManager sessionManager = SessionManager.getSessionManager();
        sessionManager.add(new Session("test"));
        LoginController loginController = new LoginController(sessionManager);

        final String httpRequest = String.join("\r\n",
                "GET /login.html HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=test",
                "",
                "");
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequest));
        HttpRequest request = new HttpRequest(bufferedReader);
        HttpResponse httpResponse = new HttpResponse();

        loginController.doGet(request, httpResponse);
        String actual = "HTTP/1.1 302 FOUND \r\n" +
                "Location: " + "/index.html \r\n" +
                "Content-Type: text/html;charset=utf-8 ";

        String response = httpResponse.getResponse();
        assertThat(actual).isEqualTo(response);
    }

    @Test
    @DisplayName("POST 요청 동작 확인")
    void doPost() throws Exception {
        SessionManager sessionManager = SessionManager.getSessionManager();
        LoginController loginController = new LoginController(sessionManager);

        final String httpRequest = String.join("\r\n",
                "POST /login HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 30",
                "",
                "account=gugu&password=password");
        BufferedReader bufferedReader = new BufferedReader(new StringReader(httpRequest));
        HttpRequest request = new HttpRequest(bufferedReader);
        HttpResponse httpResponse = new HttpResponse();

        loginController.doPost(request, httpResponse);
        String response = httpResponse.getResponse();
        assertThat(response)
                .contains("HTTP/1.1 302 FOUND")
                .contains("Location: /index.html")
                .contains("Content-Type: text/html;charset=utf-8")
                .containsPattern("Set-Cookie: JSESSIONID=");
    }
}
