package org.apache.catalina;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class CookieTest {

    @DisplayName("쿠키 속성은 ; 를 기준으로 구분하며 =를 기준으로 키와 값으로 구분한다.")
    @Test
    void parse() {
        Cookie cookie = Cookie.parse("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        assertAll(
                () -> assertThat(cookie.getValue("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(cookie.getValue("tasty_cookie")).isEqualTo("strawberry"),
                () -> assertThat(cookie.getValue("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }
}
