package org.apache.coyote.http;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpCookieTest {

    @Test
    @DisplayName("쿠키 요청이 null 이거나 빈 문자열인 경우 예외를 발생한다.")
    void validateCookie() {
        String wrongCookie = "";

        assertThatThrownBy(() -> HttpCookie.of(wrongCookie))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("정상적인 입력을 파싱하여 쿠키를 생성한다.")
    void cookie() {
        String test = "delicious=cake; spicy=kimchi";

        HttpCookie cookie = HttpCookie.of(test);

        assertThat(cookie.hasCookieName("delicious")).isTrue();
        assertThat(cookie.getCookieValue("spicy")).isEqualTo("kimchi");
    }
}
