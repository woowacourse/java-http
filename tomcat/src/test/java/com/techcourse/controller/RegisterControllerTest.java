package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@DisplayName("회원가입 컨트롤러")
class RegisterControllerTest {

    private final Controller staticResourceController = mock(Controller.class);
    private final RegisterController controller = new RegisterController(staticResourceController);

    @Test
    @DisplayName("GET 요청은 회원가입 정적 리소스 응답을 반환한다")
    void returnsStaticResourceResponseForGet() throws Exception {
        // given
        final var request = request("GET", "");
        final var expected = HttpResponse.redirect("/register.html");
        when(staticResourceController.service(request)).thenReturn(expected);

        // when
        final var response = controller.service(request);

        // then
        assertThat(response).isSameAs(expected);
    }

    @Test
    @DisplayName("회원가입에 성공하면 인덱스 페이지로 리다이렉트한다")
    void redirectsToIndexAfterSuccessfulRegistration() throws Exception {
        // given
        final var body = "account=register-controller&password=secret&email=user%40example.com";
        final var request = request("POST", body);

        // when
        final var response = controller.service(request);

        // then
        assertThat(response.status()).isEqualTo(HttpStatus.FOUND);
    }

    @Test
    @DisplayName("회원가입 요청의 사용자를 저장한다")
    void savesUserFromRegistrationRequest() throws Exception {
        // given
        final var account = "register-saved-user";
        final var body = "account=" + account + "&password=secret&email=saved%40example.com";
        final var request = request("POST", body);

        // when
        controller.service(request);

        // then
        assertThat(InMemoryUserRepository.findByAccount(account)).isPresent();
    }

    @ParameterizedTest(name = "{displayName} | 요청 본문: {0}")
    @ValueSource(strings = {
            "password=secret&email=user%40example.com",
            "account=missing-password&email=user%40example.com",
            "account=missing-email&password=secret",
            "invalid-parameter"
    })
    @DisplayName("회원가입 정보가 올바르지 않으면 400 Bad Request를 응답한다")
    void returnsBadRequestForInvalidRegistration(final String body) throws Exception {
        // given
        final var request = request("POST", body);

        // when
        final var response = controller.service(request);

        // then
        assertThat(response.status()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    private HttpRequest request(final String method, final String body) throws Exception {
        final var rawRequest = String.join("\r\n",
                method + " /register HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: " + body.length(),
                "",
                body);
        return HttpRequest.readFrom(new BufferedReader(new StringReader(rawRequest))).orElseThrow();
    }
}
