package com.techcourse.servlet;

import static org.assertj.core.api.Assertions.assertThat;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.ByteArrayInputStream;
import java.util.Optional;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class LoginHttpServletTest {

    @Test
    @DisplayName("URI와 Method가 일치하는지 확인한다.")
    void canService() {
        LoginHttpServlet loginRequestHandler = new LoginHttpServlet();
        byte[] bytes = """
                POST /login HTTP/1.1\r
                Host: localhost:8080\r
                """.getBytes();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(bytes));
        assertThat(loginRequestHandler.canService(request)).isTrue();
    }

    @Test
    @DisplayName("로그인에 실패하는 경우 401.html을 리턴한다.")
    void failLogin() {
        LoginHttpServlet loginRequestHandler = new LoginHttpServlet();
        byte[] bytes = """
                GET /login?account=abc HTTP/1.1\r
                Host: localhost:8080\r
                """.getBytes();
        HttpRequest request = new HttpRequest(new ByteArrayInputStream(bytes));
        HttpResponse response = new HttpResponse();
        loginRequestHandler.service(request, response);
        assertThat(response.getHeader("Location")).isEqualTo("/401.html");
    }

    @Test
    @DisplayName("로그인에 성공하는 경우 index.html을 반환한다.")
    void successLogin() {
        LoginHttpServlet loginRequestHandler = new LoginHttpServlet();
        MockedStatic<InMemoryUserRepository> repository = Mockito.mockStatic(InMemoryUserRepository.class);
        User user = new User(1L, "abc", "123", "email");
        repository.when(() -> InMemoryUserRepository.findByAccount("abc"))
                .thenReturn(Optional.of(user));
        byte[] bytes = """
                GET /login HTTP/1.1\r
                Host: localhost:8080\r
                Content-Length: 17\r
                \r
                account=abc&password=123
                """.trim().getBytes();

        HttpRequest request = new HttpRequest(new ByteArrayInputStream(bytes));
        request.setManager(new SessionManager());
        HttpResponse response = new HttpResponse();
        loginRequestHandler.service(request, response);
        assertThat(response.getHeader("Location")).isEqualTo("/index.html");
        repository.close();
    }
}
