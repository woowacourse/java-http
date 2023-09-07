package org.apache.coyote.http11.common.header;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SetCookieTest {

    @Test
    @DisplayName("주어진 HttpCookie를 할당하는 문자열을 변환한다.")
    void setCookieToString() {
        // given
        final String cookieString = "yummy_cookie=choco";
        final SetCookie setCookie = new SetCookie(HttpCookie.from(cookieString));
        final String expect = "Set-Cookie: " + cookieString + " ";

        // when
        final String actual = setCookie.convertToString();

        // then
        assertThat(actual).isEqualTo(expect);
    }
}
