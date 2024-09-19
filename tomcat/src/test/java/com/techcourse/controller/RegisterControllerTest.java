package com.techcourse.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.techcourse.exception.DuplicatedAccountException;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import support.FakeRequests;

class RegisterControllerTest {

    @DisplayName("이미 있는 유저명으로 회원가입을 하면 예외가 발생한다")
    @Test
    void doPost() {
        HttpRequest httpRequest = new HttpRequest(FakeRequests.validRegisterGuguRequest);
        HttpResponse httpResponse = new HttpResponse();

        Assertions.assertThatThrownBy(() -> new RegisterController("/register").doPost(httpRequest, httpResponse))
                .isInstanceOf(DuplicatedAccountException.class);
    }
}
