package org.apache.coyote.http11.common;

import org.apache.coyote.http11.common.header.HttpCookie;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpSetCookieTest {

    @Test
    @DisplayName("쿠키 문자열을 파싱하여 저장한다.")
    void stringToHttpCookie() {
        // given
        final String cookieString = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        final HttpCookie actual = HttpCookie.from(cookieString);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getCookie()).hasSize(3);
            softAssertions.assertThat(actual.getCookie().get("yummy_cookie")).isEqualTo("choco");
            softAssertions.assertThat(actual.getCookie().get("tasty_cookie")).isEqualTo("strawberry");
            softAssertions.assertThat(actual.getCookie().get("JSESSIONID"))
                          .isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        });
    }
}
