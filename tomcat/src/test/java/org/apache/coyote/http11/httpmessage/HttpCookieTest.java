package org.apache.coyote.http11.httpmessage;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("key가 jsessionid인 value를 불러온다.")
    void get_authorized_cookie() {
        // given
        final HttpCookie cookie = HttpCookie.from("JSESSIONID=ako");

        // when
        final String result = cookie.getAuthorizedCookie();

        // then
        assertThat(result).isEqualTo("ako");
    }
}
