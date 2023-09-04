package org.apache.coyote.http11.cookie;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpCookieTest {

    @Test
    void 쿠키를_파싱하여_반환할_수_있다() {
        // given
        String jSessionId = "656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie httpCookie = HttpCookie.from("yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        // then
        assertThat(httpCookie.getValue("JSESSIONID")).isEqualTo(jSessionId);
    }

}
