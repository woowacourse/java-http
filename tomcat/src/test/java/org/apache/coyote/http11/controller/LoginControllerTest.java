package org.apache.coyote.http11.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.http11.HttpHeaders;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.BaseHttpTest;

class LoginControllerTest extends BaseHttpTest {

    private final LoginController controller = LoginController.getInstance();

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

        HttpResponse response = controller.loginView();

        assertThat(response.serialize()).isEqualTo(expected);
    }

    @DisplayName("유저의 로그인 체크 후, /index.html로 리다이렉트한다")
    @Test
    void checkLogin() throws URISyntaxException, IOException {
        User user = new User("testAccount", "testPassword", "testEmail");
        InMemoryUserRepository.save(user);

        HttpRequest validLoginRequest = new HttpRequest(
                new RequestLine("GET " + "/login?account=testAccount&password=testPassword" + " HTTP/1.1 "),
                new HttpHeaders(Map.of("Content-Type", "text/html; charset=utf-8")),
                Optional.empty()
        );

        HttpResponse response = controller.checkLogin(validLoginRequest);

        String expected = resolve302Response("/index.html");
        assertThat(response.serialize()).contains(expected);
    }
}
