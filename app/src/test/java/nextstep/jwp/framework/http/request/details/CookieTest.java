package nextstep.jwp.framework.http.request.details;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CookieTest {

    @DisplayName("쿠키 헤더를 통해 쿠키 키-값으로 분리할 수 있다.")
    @Test
    void cookieParse() {
        final Cookie cookie = Cookie.of("yummy_cookie=choco; tasty_cookie=strawberry");
        assertThat(cookie.searchValue("yummy_cookie")).isEqualTo("choco");
        assertThat(cookie.searchValue("tasty_cookie")).isEqualTo("strawberry");
    }
}
