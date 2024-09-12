package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.HttpRequestHeaders;
import org.apache.coyote.http11.controller.login.LoginController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.BaseHttpTest;

class LoginControllerTest extends BaseHttpTest {

    private final LoginController controller = LoginController.getInstance();

    @AfterEach
    void tearDown() {
        InMemoryUserRepository.clear();
    }

    @DisplayName("url에 login이 포함되어 있다면 LoginContorller가 다룰 수 있는 요청임을 판단한다.")
    @Test
    void canHandle() {
        String url = "/login";
        String otherUrl = "/coliUrl";

        assertAll(
                () -> assertThat(controller.canHandle(url)).isTrue(),
                () -> assertThat(controller.canHandle(otherUrl)).isFalse()
        );
    }

    @DisplayName("login.html을 반환한다")
    @Test
    void loginView() throws URISyntaxException, IOException {
        URL url = getClass().getClassLoader().getResource("static/login.html");
        String expected = resolve200Response("html", url);
        HttpRequest validLoginRequest = new HttpRequest(
                new RequestLine("GET /login HTTP/1.1 "),
                new HttpRequestHeaders(Map.of("Content-Type", "text/html;charset=utf-8")),
                Optional.empty()
        );
        HttpResponse response = new HttpResponse();

        controller.loginView(validLoginRequest, response);

        assertThat(response.serialize()).isEqualTo(expected);
    }

    @DisplayName("유저의 로그인 체크 후, /index.html로 리다이렉트한다")
    @Test
    void checkLogin() {
        User user = new User("testAccount", "testPassword", "testEmail");
        InMemoryUserRepository.save(user);

        HttpRequest validLoginRequest = new HttpRequest(
                new RequestLine("GET " + "/login?account=testAccount&password=testPassword" + " HTTP/1.1 "),
                new HttpRequestHeaders(Map.of("Content-Type", "text/html; charset=utf-8")),
                Optional.empty()
        );
        HttpResponse response = new HttpResponse();

        controller.checkLogin(validLoginRequest, response);

        String expected = resolve302Response("/index.html");
        assertThat(response.serialize()).contains(expected);
    }

    @DisplayName("잘못된 로그인 시도 시, SecurityException을 반환한다")
    @Test
    void checkLoginFail() {
        User user = new User("testAccount", "testPassword", "testEmail");
        InMemoryUserRepository.save(user);

        HttpRequest validLoginRequest = new HttpRequest(
                new RequestLine("GET " + "/login?account=wrongAccount&password=wrongPassword" + " HTTP/1.1 "),
                new HttpRequestHeaders(Map.of("Content-Type", "text/html; charset=utf-8")),
                Optional.empty()
        );
        HttpResponse response = new HttpResponse();

        assertThatThrownBy(() -> controller.checkLogin(validLoginRequest, response))
                .isInstanceOf(SecurityException.class);
    }
}
