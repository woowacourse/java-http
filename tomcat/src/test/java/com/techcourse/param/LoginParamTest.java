package com.techcourse.param;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LoginParamTest {

    @Test
    @DisplayName("로그인 요청에서 적절한 값을 찾아 저장한다.")
    void makeLoginParam() {
        HttpRequest httpRequest = HttpRequestFixture.POST_LOGIN_PATH_REQUEST;

        LoginParam loginParam = new LoginParam(httpRequest);

        assertAll(
                () -> assertEquals(loginParam.getAccount(), "polla"),
                () -> assertEquals(loginParam.getPassword(), "password")
        );
    }
}
