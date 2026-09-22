package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.http11.response.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static support.HttpRequestFixture.get;
import static support.HttpRequestFixture.post;

class RegisterControllerTest {

    private final RegisterController registerController = new RegisterController();

    @Test
    @DisplayName("GET으로 요청하면 회원가입 페이지를 반환한다.")
    void getRegisterPage() throws IOException {
        assertThat(registerController.doService(get("/register"), new HttpResponse())).isEqualTo("/register.html");
    }

    @Test
    @DisplayName("POST로 요청하면 사용자를 저장하고 메인 페이지로 리다이렉트한다.")
    void register() throws IOException {
        // when
        String viewName = registerController.doService(
                post("/register", "account=register-test&password=register-password&email=test%40example.com"),
                new HttpResponse());

        // then
        assertThat(viewName).isEqualTo("redirect:/index.html");
        assertThat(InMemoryUserRepository.findByAccount("register-test"))
                .hasValueSatisfying(user -> assertThat(user.checkPassword("register-password")).isTrue());
    }

}
