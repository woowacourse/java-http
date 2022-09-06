package org.apache.coyote.http11;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("Cookie 헤더의 value가 들어오면 파싱하여 map으로 저장한다.")
    @Test
    void Cookie_헤더의_value가_들어오면_파싱하여_map으로_저장한다() {
        // given
        String cookieValue = "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie actual = new HttpCookie(cookieValue);

        // then
        Assertions.assertThat(actual.getValues().size()).isEqualTo(3);
    }
}
