package org.apache.coyote.http11.component.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    @DisplayName("쿠키 값 파싱한다.")
    void parse_cookie() {
        // given
        final var plaintext = "Cookie: JSESSIONID=hell; name=fram";

        // when
        final var cookie = new Cookie(plaintext);

        // then
        assertThat(cookie.get("JSESSIONID")).isEqualTo("hell");
    }

    @Test
    @DisplayName("없는 값일 경우 예외를 던진다.")
    void return_blank_when_does_not_exist_cookie_name() {
        // given
        final var plaintext = "Cookie: JSESSIONID=hell; name=fram";
        final var cookie = new Cookie(plaintext);

        // when & then
        assertThatThrownBy(() -> cookie.get("22"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
