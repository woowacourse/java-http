package org.apache.coyote.coockie;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void 세션이_있으면_true() {
        // given
        String rawCookies = "JSESSIONID=1234; wrongCookie=value";

        // when
        HttpCookie httpCookie = new HttpCookie(rawCookies);

        // then
        assertThat(httpCookie.hasSessionId()).isTrue();
    }

    @Test
    void 세션이_없으면_false() {
        // given
        String rawCookies = "wrongCookie=value";

        // when
        HttpCookie httpCookie = new HttpCookie(rawCookies);

        // then
        assertThat(httpCookie.hasSessionId()).isFalse();
    }

    @Test
    void 세션ID를_반환한다() {
        // given
        String rawCookies = "JSESSIONID=1234;";

        // when
        HttpCookie httpCookie = new HttpCookie(rawCookies);

        // then
        assertThat(httpCookie.getSessionId()).isEqualTo("1234;");
    }

    @Test
    void 쿠키_문자열을_반환한다() {
        // given
        String rawCookies = "JSESSIONID=1234;";

        // when
        HttpCookie httpCookie = new HttpCookie(rawCookies);

        // then
        assertThat(httpCookie.getResponse()).contains("JSESSIONID=1234;");
    }
}
