package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    void 쿠키를_이름으로_찾는다() {
        HttpCookie cookies = new HttpCookie(
                "yummy_cookie=choco; JSESSIONID=session-id"
        );

        assertThat(cookies.findValue("JSESSIONID"))
                .contains("session-id");
        assertThat(cookies.findValue("unknown"))
                .isEmpty();
    }
}
