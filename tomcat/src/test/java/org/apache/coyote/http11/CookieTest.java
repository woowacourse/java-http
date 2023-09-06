package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    void 쿠키_파싱_테스트() {
        Cookie cookie = new Cookie("logined=true; Path=/; Domain=localhost; Max-Age=0; Expires=Thu, 01 Jan 1970 00:00:00 GMT; HttpOnly");

        assertAll(
                () -> assertThat(cookie.getValue("logined")).isEqualTo("true"),
                () -> assertThat(cookie.getValue("Domain")).isEqualTo("localhost")
        );
    }
}
