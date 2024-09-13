package org.apache.coyote.request.body;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.fixture.RequestBodyFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestBodyTest {

    @Test
    @DisplayName("요청의 body를 키를 기준으로 파싱하여 저장한다.")
    void parseRequestBody() {
        RequestBody body = RequestBodyFixture.REGISTERED_USER_BODY;

        assertAll(
                () -> assertEquals(body.getValue("account"), "gugu"),
                () -> assertEquals(body.getValue("email"), "hkkang@woowahan.com"),
                () -> assertEquals(body.getValue("password"), "password")
        );
    }
}
