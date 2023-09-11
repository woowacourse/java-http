package org.apache.coyote.http11.common;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.apache.coyote.http11.response.ResponseHeaderKey.JSESSION;

@SuppressWarnings("NonAsciiCharacters")
class CookieTest {

    @Test
    void 쿠키의_value를_반환한다() {
        String jsessionKey = JSESSION.getResponseHeaderName();
        String jsessionValue = "656cef62-e3c4-40bc-a8df-94732920ed46";
        String cookieValue = "yummy_cookie=choco; tasty_cookie=strawberry; " + jsessionKey + "=" + jsessionValue;

        Cookie cookie = Cookie.from(cookieValue);

        Assertions.assertThat(cookie.findByKey(jsessionKey)).isEqualTo(jsessionValue);
    }
}
