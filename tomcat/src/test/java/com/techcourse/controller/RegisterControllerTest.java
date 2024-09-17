package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest {
    private final RegisterController controller;

    public RegisterControllerTest() {
        this.controller = RegisterController.getInstance();
    }

    @DisplayName("register로 GET 요청을 보내면 register.html을 200으로 응답한다")
    @Test
    void getRegister() {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.GET);
        request.setPath("/register");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getResourceName())
                        .isEqualTo("/register.html"),
                () -> assertThat(response.getHttpStatus())
                        .isEqualTo(HttpStatus.OK)
        );
    }

    @DisplayName("register로 POST 요청을 보내면 index.html로 리다이렉트한다")
    @Test
    void postRegister() {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.POST);
        request.setPath("/register");
        request.setBody("account=kiki&password=password&email=kiki@naver.com");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertAll(
                () -> assertThat(response.getLocation())
                        .isEqualTo("/index.html"),
                () -> assertThat(response.getHttpStatus())
                        .isEqualTo(HttpStatus.FOUND)
        );
    }

    @DisplayName("회원가입에 성공하면 InMemoryRepository에 저장된다")
    @Test
    void saveInRepository() {
        // given
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.POST);
        request.setPath("/login");
        request.setBody("account=kiki&password=password&email=kiki@naver.com");
        HttpResponse response = new HttpResponse();

        // when
        controller.service(request, response);

        // then
        assertThat(InMemoryUserRepository.findByAccount("kiki"))
                .isPresent();
    }

    @DisplayName("이미 존재하는 계정으로 회원가입을 요청하면 예외로 처리한다.")
    @Test
    void duplicatedAccount() {
        User user = new User("kiki", "password", "kiki@naver.com");
        InMemoryUserRepository.save(user);
        HttpRequest request = new HttpRequest();
        request.setMethod(HttpMethod.POST);
        request.setPath("/login");
        request.setBody("account=kiki&password=password&email=kiki@naver.com");
        HttpResponse response = new HttpResponse();

        assertThatThrownBy(() -> controller.service(request, response))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("이미 존재하는 계정입니다.");
    }
}
