package org.apache.coyote.http11.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    void 문자열을_파싱해서_키_값_형태로_저장한다() {
        final String string = "FIRSTCOOKIE=1234; SECONDCOOKIE=abcd";
        final Cookie cookie = Cookie.from(string);

        assertThat(cookie.getAttribute("FIRSTCOOKIE")).isEqualTo("1234");
        assertThat(cookie.getAttribute("SECONDCOOKIE")).isEqualTo("abcd");
    }
}
