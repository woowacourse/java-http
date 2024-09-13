package org.apache.coyote.http11;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @DisplayName("JSESSION 쿠키인지 확인한다")
    @Test
    void isJSessionCookie() {
        String cookieValue = "JSESSIONID=test";
        Cookie cookie = new Cookie(cookieValue);
        Assertions.assertThat(cookie.isJSessionCookie()).isTrue();
    }

    @DisplayName("JSESSIONID의 값을 가져온다.")
    @Test
    void getJSessionId() {
        String cookieValue = "JSESSIONID=test";
        Cookie cookie = new Cookie(cookieValue);
        Assertions.assertThat(cookie.getJSessionId()).isEqualTo("test");
    }
}
