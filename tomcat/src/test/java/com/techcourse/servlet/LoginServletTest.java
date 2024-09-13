package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class LoginServletTest {

    @Test
    @DisplayName("세션이 유효한 경우, 홈 페이지로 리다이렉트된다.")
    void redirectOnValidSession() {
        SessionManager manager = new SessionManager(() -> "hoony");
        Session session = manager.createSession();
        session.setAttribute("user", new User("aru", "dong", "hoony"));
        manager.add(session);

        LoginServlet handler = new LoginServlet();
        byte[] requestBytes = """
                GET /login HTTP/1.1\r
                Cookie: JSESSIONID=hoony\r
                """.getBytes();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));

        request.setManager(manager);
        HttpResponse response = new HttpResponse();
        handler.service(request, response);
        assertThat(response.getHeader(HttpHeaders.LOCATION)).isEqualTo("/index.html");

        manager.remove(session);
    }

    @Test
    @DisplayName("세션이 유효하지 않은 경우, 로그인 페이지를 불러온다.")
    void loadOnInvalidSession() {
        SessionManager manager = new SessionManager(() -> "hoony");
        LoginServlet handler = new LoginServlet();
        byte[] requestBytes = """
                GET /login HTTP/1.1\r
                Cookie: JSESSIONID=hoony\r
                """.getBytes();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));

        request.setManager(manager);
        HttpResponse response = new HttpResponse();
        handler.service(request, response);
        assertThat(response.getHeader(HttpHeaders.LOCATION)).isEqualTo("/login.html");
    }

    @Test
    @DisplayName("세션이 존재하지 않는 경우, 로그인 페이지를 불러온다.")
    void loadOnNoSession() {
        SessionManager manager = new SessionManager(() -> "hoony");
        LoginServlet handler = new LoginServlet();
        byte[] requestBytes = """
                GET /login HTTP/1.1\r
                """.getBytes();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(requestBytes));

        request.setManager(manager);
        HttpResponse response = new HttpResponse();
        handler.service(request, response);
        assertThat(response.getHeader(HttpHeaders.LOCATION)).isEqualTo("/login.html");
    }

    @Test
    @DisplayName("로그인에 실패하는 경우 401.html을 리턴한다.")
    void failLogin() {
        LoginServlet loginRequestHandler = new LoginServlet();
        byte[] bytes = """
                POST /login?account=abc HTTP/1.1\r
                Host: localhost:8080\r
                """.getBytes();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(bytes));
        request.setManager(new SessionManager(() -> "hoony"));
        HttpResponse response = new HttpResponse();
        loginRequestHandler.service(request, response);
        assertThat(response.getHeader(HttpHeaders.LOCATION)).isEqualTo("/401.html");
    }

    @Test
    @DisplayName("로그인에 성공하는 경우 index.html을 반환한다.")
    void successLogin() {
        LoginServlet loginRequestHandler = new LoginServlet();
        MockedStatic<InMemoryUserRepository> repository = Mockito.mockStatic(InMemoryUserRepository.class);
        User user = new User(1L, "abc", "123", "email");
        repository.when(() -> InMemoryUserRepository.findByAccount("abc"))
                .thenReturn(Optional.of(user));
        byte[] bytes = """
                POST /login HTTP/1.1\r
                Host: localhost:8080\r
                Content-Length: 17\r
                \r
                account=abc&password=123
                """.trim().getBytes();

        HttpRequest request = new HttpRequest(new ByteArrayInputStream(bytes));
        request.setManager(new SessionManager(() -> "hoony"));
        HttpResponse response = new HttpResponse();
        loginRequestHandler.service(request, response);
        assertThat(response.getHeader(HttpHeaders.LOCATION)).isEqualTo("/index.html");
        repository.close();
    }
}
