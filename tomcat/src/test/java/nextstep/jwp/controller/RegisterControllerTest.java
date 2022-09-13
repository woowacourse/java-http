package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.*;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import nextstep.jwp.db.InMemoryUserRepository;
import support.TestRequest;

class RegisterControllerTest {

    @Test
    @DisplayName("GET 요청이 올 경우 register.html 파일을 응답한다.")
    void doGet() {
        // given
        final RegisterController controller = new RegisterController();
        final HttpRequest request = TestRequest.generateWithUri("/register");

        // when
        final HttpResponse response = controller.doGet(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 24 ",
            "",
            "register controller test");

        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }

    @Test
    @DisplayName("회원가입이 성공한 경우 Redirect 한다.")
    void doPost() {
        // given
        final RegisterController controller = new RegisterController();
        final HttpRequest request = TestRequest.generateWithUriAndUserInfo("/register",
            "test account", "test password", "test email");

        // when
        final HttpResponse response = controller.doPost(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 302 FOUND ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 0 ",
            "Location: index ",
            "", "");
        final boolean isRegistered = InMemoryUserRepository.findByAccount("test account")
            .isPresent();

        // then
        Assertions.assertAll(
            () -> assertThat(isRegistered).isTrue(),
            () -> assertThat(response.getBytes()).isEqualTo(expected.getBytes())
        );
    }

    @Test
    @DisplayName("이미 존재하는 회원인 경우 예외페이지를 반환한다.")
    void doPostWhenUserInfoAlreadyRegistered() {
        // given
        final RegisterController controller = new RegisterController();
        final HttpRequest request = TestRequest.generateWithUriAndUserInfo("/register",
            "gugu", "test password", "test email");

        // when
        final HttpResponse response = controller.doPost(request);
        final String expected = String.join("\r\n",
            "HTTP/1.1 200 OK ",
            "Content-Type: text/html;charset=utf-8 ",
            "Content-Length: 35 ",
            "",
            "User account has already registered");

        // then
        assertThat(response.getBytes()).isEqualTo(expected.getBytes());
    }
}
