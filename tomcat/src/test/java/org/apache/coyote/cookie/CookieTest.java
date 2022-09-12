package org.apache.coyote.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    void cookie를_생성한다() {
        Cookie actual = Cookie.from("JSESSIONID=jsession-Cookie");

        assertThat(actual).isEqualTo(new Cookie("JSESSIONID", "jsession-Cookie"));
    }

    @Test
    void responseHeader로_변환한다() {
        Cookie cookie = new Cookie("JSESSIONID", "jsession-Cookie");

        assertThat(cookie.toHeader()).isEqualTo("JSESSIONID=jsession-Cookie");
    }
}
