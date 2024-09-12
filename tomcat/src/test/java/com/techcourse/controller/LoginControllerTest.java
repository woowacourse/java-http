package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.catalina.session.SessionService;
import org.apache.coyote.http11.request.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class LoginControllerTest {

    private final User user1 = new User("account", "password", "email");
    private final User user2 = new User("account2", "password2", "email2");
    private final Session session = new Session("sessionId");
    private final SessionManager sessionManager = new SessionManager();
    private final LoginController loginController = new LoginController(new SessionService(() -> session));

    @BeforeEach
    void saveUser() {
        InMemoryUserRepository.save(user1);
        InMemoryUserRepository.save(user2);
    }

    @AfterEach
    void deleteUser() {
        InMemoryUserRepository.delete(user1);
        InMemoryUserRepository.delete(user2);
        session.invalidate();
        sessionManager.remove(session);
    }

    @DisplayName("로그인 성공 시 올바르게 응답을 보낸다.")
    @Test
    void loginSuccess() {
        HttpRequest request = new HttpRequest(
                RequestLine.of("POST /login HTTP/1.1 "),
                new HttpHeaders(Map.of()),
                "account=account&password=password"
        );
        HttpResponse response = new HttpResponse();

        loginController.service(request, response);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.FOUND,
                Map.of("Location", "/index.html",
                        "Set-Cookie", "JSESSIONID=" + session.getId())
        );
        assertThat(response).isEqualTo(expectedResponse);
    }

    @DisplayName("로그인 실패 시 예외가 발생한다.")
    @ParameterizedTest()
    @ValueSource(strings = {"account=account1&password=password", "account=account1&password=password"})
    void loginFail(String body) {
        HttpRequest request = new HttpRequest(
                RequestLine.of("POST /login HTTP/1.1 "),
                new HttpHeaders(Map.of()),
                body
        );

        HttpResponse response = new HttpResponse();
        loginController.service(request, response);

        HttpResponse expectedResponse = HttpResponse.createRedirectResponse(HttpStatus.FOUND, "/401.html");
        assertThat(response).isEqualTo(expectedResponse);
    }

    @DisplayName("세션이 존재하는 클라이언트가 로그인 요청 시 올바르게 세션 데이터를 업데이트한다.")
    @Test
    void sessionUserLogin() {
        session.setAttribute("user", user1);
        sessionManager.add(session);
        HttpRequest request = new HttpRequest(
                RequestLine.of("POST /login HTTP/1.1 "),
                new HttpHeaders(Map.of("Cookie", "JSESSIONID=" + session.getId())),
                "account=account2&password=password2"
        );
        HttpResponse response = new HttpResponse();

        loginController.service(request, response);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.FOUND,
                Map.of("Location", "/index.html")
        );
        assertAll(
                () -> assertThat(response).isEqualTo(expectedResponse),
                () -> {
                    HttpSession session = sessionManager.findSession(this.session.getId());
                    User user = (User) session.getAttribute("user");
                    assertThat(user.getAccount()).isEqualTo(user2.getAccount());
                }
        );
    }

    @DisplayName("로그인 페이지를 올바르게 반환한다.")
    @Test
    void loginGet() {
        HttpRequest request = new HttpRequest(
                RequestLine.of("GET /login HTTP/1.1 "),
                new HttpHeaders(Map.of()),
                ""
        );
        HttpResponse response = new HttpResponse();

        loginController.service(request, response);

        ResponseFile file = ResponseFile.of("/login.html");
        Map<String, String> headers = Map.of(
                "Content-Type", file.getContentType(),
                "Content-Length", String.valueOf(file.getContentLength())
        );
        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.OK,
                headers,
                file.getContent()
        );
        assertThat(response).isEqualTo(expectedResponse);
    }

    @DisplayName("로그인 된 사용자가 로그인 페이지를 조회할 시 리다이렉트한다.")
    @Test
    void loginGetRedirect() {
        session.setAttribute("user", user1);
        sessionManager.add(session);
        HttpRequest request = new HttpRequest(
                RequestLine.of("GET /login HTTP/1.1 "),
                new HttpHeaders(Map.of()),
                ""
        );
        HttpResponse response = new HttpResponse();

        loginController.service(request, response);

        HttpResponse expectedResponse = new HttpResponse(
                HttpStatus.FOUND,
                Map.of("Location", "/index.html")
        );
        assertThat(response).isEqualTo(expectedResponse);
    }
}
