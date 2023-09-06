package org.apache.coyote.httprequest.header;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
class CookieRequestHeaderTest {

    @Test
    void JSessionId가_존재하면_JSessionID를_반환한다() {
        // given
        final String sessionValue = "abcde";
        final String cookieValue = "JSESSIONID=" + sessionValue;

        // when
        final CookieRequestHeader cookie = CookieRequestHeader.from(cookieValue);

        // then
        assertThat(cookie.hasJSessionId()).isTrue();
        assertThat(cookie.getJSessionId()).isEqualTo(sessionValue);
    }

    @Test
    void JSessionId가_존재하지_않으면_null을_반환한다() {
        // given, when
        final CookieRequestHeader cookie = CookieRequestHeader.blank();

        // then
        assertThat(cookie.hasJSessionId()).isFalse();
        assertThat(cookie.getJSessionId()).isNull();
    }
}
