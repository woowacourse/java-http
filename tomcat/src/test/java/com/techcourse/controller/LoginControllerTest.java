package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.apache.catalina.Session;
import org.apache.catalina.manager.SessionManager;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestBody;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;

class LoginControllerTest {
    private LoginController loginController;
    private MockedStatic<InMemoryUserRepository> mockedRepository;

    @BeforeEach
    void setUp() {
        loginController = LoginController.getInstance();
        mockedRepository = mockStatic(InMemoryUserRepository.class);
    }

    @AfterEach
    void clear() {
        mockedRepository.close();
    }

    @DisplayName("성공적인 로그인 요청에 대해 index.html로 리다이랙트한다.")
    @Test
    void loginSuccess() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1 ");
        String body = "account=validUser&password=correctPassword";
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length
        ));
        RequestBody requestBody = new RequestBody(body);
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        User mockUser = new User(1L, "validUser", "correctPassword", "correctEmail");
        mockedRepository.when(() -> InMemoryUserRepository.findByAccount("validUser"))
                .thenReturn(Optional.of(mockUser));


        // when
        loginController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND";
        String expectedLocationHeader = "Location: index.html";
        String expectedCookie = "Set-Cookie: JSESSIONID=";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader,
                expectedCookie
        );
    }

    @DisplayName("로그인이 잘못되면 401.html로 리다이랙트한다.")
    @Test
    void loginFailedInvalidPassword() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("POST /login HTTP/1.1 ");
        String body = "account=validUser&password=wrongPassword";
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length
        ));
        RequestBody requestBody = new RequestBody(body);
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, requestBody);
        HttpResponse httpResponse = new HttpResponse();

        User mockUser = new User(1L, "validUser", "correctPassword", "correctEmail");
        mockedRepository.when(() -> InMemoryUserRepository.findByAccount("validUser"))
                .thenReturn(Optional.of(mockUser));

        // when
        loginController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: 401.html ";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader
        );
    }

    @DisplayName("쿠키가 없으면 로그인 화면을 응답한다.")
    @Test
    void loginPage() throws IOException {
        // given
        RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1 ");
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive "
        ));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND \r\n";
        String expectedLocationHeader = "Location: login.html ";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader
        );
    }

    @DisplayName("쿠키가 있으면 대쉬보드 화면을 응답한다.")
    @Test
    void homePage() throws IOException {
        // given
        Session session = Session.createRandomSession();
        session.setAttribute("user", "validUser");
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        RequestLine requestLine = RequestLine.from("GET /login HTTP/1.1 ");
        HttpHeaders headers = HttpHeaders.from(List.of(
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Cookie: JSESSIONID=" + session.getId()
        ));
        HttpRequest httpRequest = new HttpRequest(requestLine, headers, new RequestBody());
        HttpResponse httpResponse = new HttpResponse();

        // when
        loginController.handle(httpRequest, httpResponse);

        // then
        String expectedResponseLine = "HTTP/1.1 302 FOUND";
        String expectedLocationHeader = "Location: index.html";

        assertThat(httpResponse.serialize()).contains(
                expectedResponseLine,
                expectedLocationHeader
        );
    }

}
