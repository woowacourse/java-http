package org.apache.coyote.http.vo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class CookieTest {

    @Test
    void 쿠키는_어떤_키값이_저장되어_있는지_알_수_있다() {
        // given
        Cookie cookie = Cookie.emptyCookie();

        // when
        cookie.put("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46");

        // then
        assertThat(cookie.hasCookie("JSESSIONID")).isTrue();
    }

    @Test
    void 쿠기는_raw_string_형식으로_반환할_수_있다() {

    }
}
