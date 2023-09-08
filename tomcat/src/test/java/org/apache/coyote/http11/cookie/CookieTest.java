package org.apache.coyote.http11.cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CookieTest {

    @DisplayName("쿠키를 생성한다")
    @Test
    void create_cookie() {
        // given
        String input = "Idea-f9653bd1=45d2c731-e442-471a-a633-2697b3386fc6; JSESSIONID=9f31b31b-6dc6-494b-938f-bc4de8c2e120";

        // when
        Cookie cookie = Cookie.from(input);

        // then
        assertThat(cookie.getJSessionId().get()).isEqualTo("9f31b31b-6dc6-494b-938f-bc4de8c2e120");
    }
}
