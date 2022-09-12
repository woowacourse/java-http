package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.model.User;
import support.TestRequest;

public class LoginControllerTest {

    @Test
    @DisplayName("Login 상태가 아닌 경우 login.html 페이지를 반환한다.")
    void doGet_notLoggedIn() {
        // given
        final LoginController controller = new LoginController();
        final HttpRequest request = TestRequest.generateWithUri("/login");

        // when
        final HttpResponse response = controller.doGet(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 21 ",
            "",
            "login controller test");

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }

    @Test
    @DisplayName("Login 상태인 경우 /index로 Redirect 한다(GET).")
    void doGet_loggedIn() {
        // given
        final LoginController controller = new LoginController();
        final SessionManager sessionManager = SessionManager.getInstance();
        final HttpRequest request = TestRequest.generateWithSession("GET", "/login", "HTTP/1.1",
            "JSESSIONID=testId");

        // when
        // SessionManager에 gugu 유저 세션 등록
        final Session session = new Session("testId");
        User loginUser = InMemoryUserRepository.findByAccount("gugu").orElseThrow();
        session.setAttribute("user", loginUser);
        sessionManager.add(session);

        final HttpResponse response = controller.doGet(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 302 FOUND ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 0 ",
            "Location: /index ",
            "", "");

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
        sessionManager.remove(session);
    }

    @Test
    @DisplayName("Login 상태인 경우 /index로 Redirect 한다(POST).")
    void doPost_loggedIn() {
        // given
        final LoginController controller = new LoginController();
        final SessionManager sessionManager = SessionManager.getInstance();
        final HttpRequest request = TestRequest.generateWithSession("POST", "/login", "HTTP/1.1",
            "JSESSIONID=testId");

        // when
        // SessionManager에 gugu 유저 세션 등록
        final Session session = new Session("testId");
        User loginUser = InMemoryUserRepository.findByAccount("gugu").orElseThrow();
        session.setAttribute("user", loginUser);
        sessionManager.add(session);

        final HttpResponse response = controller.doPost(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 302 FOUND ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 0 ",
            "Location: /index ",
            "", "");

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
        sessionManager.remove(session);
    }

    @Test
    @DisplayName("유저 정보가 일치하지 않을 경우 /401.html로 Redirect 한다.")
    void doPost_invalidUser() {
        // given
        final LoginController controller = new LoginController();
        final HttpRequest request = TestRequest.generateWithUriAndUserInfo(
            "/login", "gugu", "invalid password", "");

        // when
        final HttpResponse response = controller.doPost(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 302 FOUND ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 0 ",
            "Location: /401.html ",
            "", "");

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }

    @Test
    @DisplayName("로그인 성공했을 경우 /index로 Redirect 한다.")
    void doPost_redirect() {
        // given
        final LoginController controller = new LoginController();
        final HttpRequest request = TestRequest.generateWithUriAndUserInfo(
            "/login", "gugu", "password", "");

        // when
        final HttpResponse response = controller.doPost(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 302 FOUND ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 0 ",
            "Location: /index ",
            "", "");

        // then
        assertThat(response.getBytes()).contains(expected.getBytes());
    }
}
