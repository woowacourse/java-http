package nextstep.jwp.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CookieTest {
    @Test
    @DisplayName("쿠키는 name과 value로 이루어져 있다.")
    void cookie() {
        // given
        String name = "key";
        String value = "value";

        // when
        Cookie cookie = new Cookie(name, value);

        // then
        assertThat(cookie.getName()).isEqualTo(name);
        assertThat(cookie.getValue()).isEqualTo(value);
    }
}
