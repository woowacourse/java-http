package nextstep.jwp.controller;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import java.io.ByteArrayOutputStream;
import java.util.Map;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.ExistUserException;
import org.apache.coyote.request.HttpHeader;
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestBody;
import org.apache.coyote.response.HttpResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {

    @BeforeEach
    void setUp() {
        InMemoryUserRepository.rollback();
    }

    @DisplayName("회원가입을 성공하면 로그를 남긴다.")
    @Test
    void register() {
        // given
        final String account = "dwoo";
        final String password = "1234";
        final String email = "dwoo@email.com";
        final String requestBody = "account=" + account + "&password=" + password + "&email=" + email;
        final HttpRequest httpRequest = HttpRequest.of("POST /register HTTP/1.1",
                HttpHeader.from(Map.of()), HttpRequestBody.from(requestBody));

        final HttpResponse httpResponse = HttpResponse.from(new ByteArrayOutputStream());
        final RegisterController registerController = new RegisterController();

        // when & then
        assertDoesNotThrow(() -> registerController.service(httpRequest, httpResponse));
    }

    @DisplayName("이미 존재하는 account로 회원가입시 ExistUserException이 발생한다.")
    @Test
    void failToLogin() {
        // given
        final String requestBody = "account=gugu&password=password&email=gugu@email.com";
        final HttpRequest httpRequest = HttpRequest.of("POST /register HTTP/1.1",
                HttpHeader.from(Map.of()), HttpRequestBody.from(requestBody));

        // when & then
        final HttpResponse httpResponse = HttpResponse.from(new ByteArrayOutputStream());
        final RegisterController registerController = new RegisterController();
        assertThatThrownBy(() -> registerController.service(httpRequest, httpResponse))
                .isInstanceOf(ExistUserException.class);
    }
}
