package com.techcourse.controller;

import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.enums.HttpMethod;
import org.apache.coyote.http11.enums.HttpStatus;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegisterControllerTest {

    @Test
    void 새로운_회원이면_저장하고_인덱스_페이지로_리다이렉트한다() throws Exception {
        // given
        final String account = "account-" + UUID.randomUUID();
        final RegisterController registerController =
                new RegisterController();
        final HttpRequest request = createRegisterRequest(account);

        // when
        final HttpResponse response = new HttpResponse();
        registerController.service(request, response);

        // then
        assertThat(response.status()).isEqualTo(HttpStatus.SEE_OTHER);
        assertThat(response.headers())
                .containsEntry("Location", "/index.html");
        assertThat(InMemoryUserRepository.findByAccount(account))
                .isPresent();
    }

    @Test
    void 이미_가입한_계정이면_500_페이지로_이동한다() throws Exception {
        // given
        final RegisterController registerController =
                new RegisterController();
        final HttpRequest request = createRegisterRequest("gugu");

        // when
        final HttpResponse response = new HttpResponse();
        registerController.service(request, response);

        // then
        assertThat(response.status())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.headers())
                .containsEntry("Location", "/500.html");
    }

    private HttpRequest createRegisterRequest(final String account) {
        return HttpRequest.builder()
                .httpMethod(HttpMethod.POST)
                .path("/register")
                .version("HTTP/1.1")
                .params(Map.of(
                        "account", account,
                        "password", "password",
                        "email", account + "@example.com"
                ))
                .build();
    }
}
