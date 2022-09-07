package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    void 헤더에서_쿠키가_파싱되는지_확인한다() {
        String cookieHeader = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        Cookie cookie = Cookie.parse(cookieHeader);

        assertThat(cookie.isContainJSessionId()).isTrue();
    }
}
