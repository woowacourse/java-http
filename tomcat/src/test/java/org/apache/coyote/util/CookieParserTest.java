package org.apache.coyote.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.coyote.common.HttpCookie;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class CookieParserTest {

    @Test
    void 성공() {
        // given
        String cookieString = "foo=bar; glen=1234";

        // when
        HttpCookie cookie = CookieParser.parse(cookieString);

        // then
        assertThat(cookie.get("foo")).isEqualTo("bar");
        assertThat(cookie.get("glen")).isEqualTo("1234");
    }
}
