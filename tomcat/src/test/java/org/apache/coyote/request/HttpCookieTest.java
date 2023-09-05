package org.apache.coyote.request;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpCookieTest {

    @Test
    void 추출할_쿠키가_있다면_HTTP_쿠키를_반환한다() {
        final String cookies = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        final HttpCookie httpCookie = HttpCookie.from(cookies);

        assertSoftly(softly -> {
            softly.assertThat(httpCookie.getCookie("yummy_cookie")).isEqualTo("choco");
            softly.assertThat(httpCookie.getCookie("tasty_cookie")).isEqualTo("strawberry");
            softly.assertThat(httpCookie.getCookie("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
        });
    }
}
