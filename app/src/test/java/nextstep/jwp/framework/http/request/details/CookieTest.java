package nextstep.jwp.framework.http.request.details;

import nextstep.jwp.framework.http.common.HttpSession;
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

    @DisplayName("쿠키에서 세션을 만들기 전에 조회하면 Null이 반환된다.")
    @Test
    void getSessionBeforeGeneration() {
        final Cookie cookie = Cookie.of("yummy_cookie=choco; tasty_cookie=strawberry");
        final HttpSession session = cookie.getSession();
        assertThat(session).isNull();
    }

    @DisplayName("쿠키에서 세션을 만든 후 조회하면 생성된 세션이 반환된다.")
    @Test
    void getSessionAfterGeneration() {
        final Cookie cookie = Cookie.of("yummy_cookie=choco; tasty_cookie=strawberry");
        cookie.generateSession();
        final HttpSession findSession = cookie.getSession();
        assertThat(findSession).isNotNull();
    }

    @DisplayName("쿠키에서 세션을 만든 후 또 다시 생성하면 이미 만들어진 세션이 반환된다.")
    @Test
    void generateSessionAfterGeneration() {
        final Cookie cookie = Cookie.of("yummy_cookie=choco; tasty_cookie=strawberry");
        final HttpSession findSession1 = cookie.generateSession();
        final HttpSession findSession2 = cookie.generateSession();
        assertThat(findSession1).isEqualTo(findSession2);
    }
}
