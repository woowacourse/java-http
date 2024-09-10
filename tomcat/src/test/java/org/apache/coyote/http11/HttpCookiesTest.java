package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @DisplayName("세션Id에 대한 쿠키를 추가할 수 있다.")
    @Test
    void addSession() {
        // given
        String expected = "sessionId";
        HttpCookies cookies = new HttpCookies();

        // when
        cookies.addSession(expected);
        String actual = cookies.getSessionId();

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
