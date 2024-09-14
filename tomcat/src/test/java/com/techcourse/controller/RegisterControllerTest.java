package com.techcourse.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http.HttpResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterControllerTest extends ControllerTest {

    private final RegisterController sut = new RegisterController();

    @Test
    @DisplayName("회원가입 성공: index로 리다이렉트")
    void register() throws Exception {
        // given
        var account = "gugu";
        var password = "password";
        var email = "hkkang@woowahan.com";
        var requestBody = "account=%s&password=%s&email=%s".formatted(account, password, email);
        var request = httpRequest(String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody));
        var response = new HttpResponse();

        // when
        sut.service(request, response);

        // then
        assertThat(response.asString())
                .contains("HTTP/1.1 302 FOUND ")
                .contains("Location: /index.html ");
    }

    @Test
    @DisplayName("회원가입 실패: 아이디가 없을 경우 IllegalArgumentException 발생")
    void register_failure_no_account() throws Exception {
        // given
        var no_account = "";
        var password = "password";
        var email = "hkkang@woowahan.com";
        var requestBody = "account=%s&password=%s&email=%s".formatted(no_account, password, email);
        var request = httpRequest(String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody));
        var response = new HttpResponse();

        // then
        assertThatThrownBy(() -> sut.service(request, response))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원가입 실패: 비밀번호가 없을 경우 IllegalArgumentException 발생")
    void register_failure_no_password() throws Exception {
        // given
        var account = "gugu";
        var no_password = "";
        var email = "hkkang@woowahan.com";
        var requestBody = "account=%s&password=%s&email=%s".formatted(account, no_password, email);
        var request = httpRequest(String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody));
        var response = new HttpResponse();

        // when & then
        assertThatThrownBy(() -> sut.service(request, response))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원가입 실패: 이메일이 없을 경우 IllegalArgumentException 발생")
    void register_failure_no_email() throws Exception {
        // given
        var account = "gugu";
        var password = "password";
        var no_email = "";
        var requestBody = "account=%s&password=%s&email=%s".formatted(account, password, no_email);
        var request = httpRequest(String.join("\r\n",
                "POST /register HTTP/1.1 ",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "Content-Length: 80 ",
                "Content-Type: application/x-www-form-urlencoded ",
                "Accept: */* ",
                "",
                requestBody));
        var response = new HttpResponse();

        // when & then
        assertThatThrownBy(() -> sut.service(request, response))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
