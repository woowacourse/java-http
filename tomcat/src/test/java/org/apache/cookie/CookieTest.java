package org.apache.cookie;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CookieTest {

    @Test
    void 전달받은_쿠키_값을_파싱해_맵에_저장해_쿠키생성() {
        String cookieValue = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        Cookie cookie = Cookie.from(cookieValue);

        assertAll(
                () -> assertThat(cookie.getCookies().get("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(cookie.getCookies().get("tasty_cookie")).isEqualTo("strawberry"),
                () -> assertThat(cookie.getCookies().get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @Test
    void 쿠키_내부에_JSESSIONID_존재여부_확인() {
        String cookieValue1 = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";
        String cookieValue2 = "yummy_cookie=choco; tasty_cookie=strawberry";

        Cookie cookie1 = Cookie.from(cookieValue1);
        Cookie cookie2 = Cookie.from(cookieValue2);

        assertThat(cookie1.hasJSessionId()).isTrue();
        assertThat(cookie2.hasJSessionId()).isFalse();
    }

}
