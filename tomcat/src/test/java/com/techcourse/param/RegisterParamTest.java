package com.techcourse.param;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.fixture.HttpRequestFixture;
import org.apache.coyote.request.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RegisterParamTest {

    private static final String EXPECT_ACCOUNT = "polla";
    private static final String EXPECT_EMAIL = "hkkang@woowahan.com";
    private static final String EXPECT_PASSWORD = "password";

    @Test
    @DisplayName("회원가입 요청에서 적절한 값을 찾아 저장한다.")
    void makeRegisterParam() {
        HttpRequest httpRequest = HttpRequestFixture.POST_NEW_USER_REGISTER_PATH_REQUEST;

        RegisterParam registerParam = new RegisterParam(httpRequest);

        assertAll(
                () -> assertEquals(registerParam.getAccount(), EXPECT_ACCOUNT),
                () -> assertEquals(registerParam.getEmail(), EXPECT_EMAIL),
                () -> assertEquals(registerParam.getPassword(), EXPECT_PASSWORD)
        );
    }
}
