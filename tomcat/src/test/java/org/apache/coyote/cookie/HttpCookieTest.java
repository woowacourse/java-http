package org.apache.coyote.cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void Cookie_헤더의_이름이_포함된_경우_예외가_발생한다() {
        String containingCookieHeaderName = "Cookie: yummy_cookie=choco";
        assertThatThrownBy(() -> HttpCookie.from(containingCookieHeaderName))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 존재하지_않는_쿠키를_조회하면_빈_값을_반환한다() {
        String cookieHeader = "yummy_cookie=choco";

        HttpCookie httpCookie = HttpCookie.from(cookieHeader);

        assertThat(httpCookie.getValue("tasty_cookie")).isEmpty();
    }

    @Test
    void 정상_생성() {
        String validCookieHeader = "yummy_cookie=choco; tasty_cookie=strawberry";

        HttpCookie httpCookie = HttpCookie.from(validCookieHeader);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(httpCookie.getValue("yummy_cookie")).hasValue("choco");
            softly.assertThat(httpCookie.getValue("tasty_cookie")).hasValue("strawberry");
        });
    }
}
