package com.techcourse.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestBody;
import org.apache.coyote.http11.request.HttpRequestHeaders;
import org.apache.coyote.http11.request.HttpRequestStartLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.util.StaticFileResponseUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoginControllerTest {

    private LoginController loginController;

    @BeforeEach
    void setUp() {
        loginController = new LoginController();
    }

    @Test
    void doGetTest_whenSessionExists_shouldRedirectToIndex() throws Exception {
        Session session = new Session("session-id");
        session.setAttribute("user", new User("account", "password", "email"));
        SessionManager.getInstance().add(session);
        HttpRequestStartLine startLine = new HttpRequestStartLine("GET", "/login", "HTTP/1.1");
        HttpRequestHeaders headers = new HttpRequestHeaders(Map.of("Cookie", "JSESSIONID=" + session.getId()));
        HttpRequestBody body = new HttpRequestBody();
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        loginController.doGet(request, response);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.getStatusCode()),
                () -> assertThat(response.getHeaders().get("Location")).isEqualTo("/index.html"));

    }

    @Test
    void doGetTest_whenSessionNotExists_shouldServeLoginPage() throws Exception {
        HttpRequestStartLine startLine = new HttpRequestStartLine("GET", "/login", "HTTP/1.1");
        HttpRequestHeaders headers = new HttpRequestHeaders();
        HttpRequestBody body = new HttpRequestBody();
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        loginController.doGet(request, response);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK.getStatusCode()),
                () -> assertThat(response.getBody()).isEqualTo(StaticFileResponseUtils.readStaticFile("/login.html"))
        );
    }

    @Test
    void doPostTest_whenLoginFails_shouldRedirectTo401() throws Exception {
        HttpRequestStartLine startLine = new HttpRequestStartLine("POST", "/login", "HTTP/1.1");
        HttpRequestHeaders headers = new HttpRequestHeaders(
                Map.of("Content-Type", "application/x-www-form-urlencoded"));
        HttpRequestBody body = new HttpRequestBody("account=wrongAccount&password=wrongPassword");
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        loginController.doPost(request, response);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.getStatusCode()),
                () -> assertThat(response.getHeaders().get("Location")).isEqualTo("/401.html")
        );
    }

    @Test
    void doPostTest_whenLoginSucceeds_shouldRedirectToIndexAndSetSession() throws Exception {
        User testUser = new User("testAccount", "password", "email");
        InMemoryUserRepository.save(testUser);

        HttpRequestStartLine startLine = new HttpRequestStartLine("POST", "/login", "HTTP/1.1");
        HttpRequestHeaders headers = new HttpRequestHeaders(
                Map.of("Content-Type", "application/x-www-form-urlencoded"));
        HttpRequestBody body = new HttpRequestBody("account=testAccount&password=password");
        HttpRequest request = new HttpRequest(startLine, headers, body);
        HttpResponse response = new HttpResponse();

        loginController.doPost(request, response);

        assertAll(
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND.getStatusCode()),
                () -> assertThat(response.getHeaders().get("Location")).isEqualTo("/index.html"),
                () -> assertThat(response.getHeaders().get("Set-Cookie")).isNotNull()
        );
    }
}

