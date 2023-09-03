package org.apache.coyote.http11.session;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpCookieTest {

    @Test
    void Cookie_헤더로부터_Cookie를_생성한다() {
        // given
        String cookieHeader = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie cookie = HttpCookie.from(cookieHeader);

        // then
        Assertions.assertThat(cookie.getJSessionId()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
