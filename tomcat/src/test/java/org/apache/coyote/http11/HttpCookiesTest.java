package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @Test
    @DisplayName("쿠키 문자열을 읽어 올바르게 객체를 생성한다.")
    void parseCookies() {
        String cookieLine = "a=1; b=2; c=3";
        HttpCookies cookies = new HttpCookies(cookieLine);
        assertThat(cookies.getCookies()).hasSize(3);
    }
}
