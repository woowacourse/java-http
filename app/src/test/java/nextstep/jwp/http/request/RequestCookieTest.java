package nextstep.jwp.http.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RequestCookie 테스트")
class RequestCookieTest {

    @DisplayName("요청 헤더 쿠키 파싱 테스트 - 쿠키가 1개일 때")
    @Test
    void parseOneCookie() {
        //given
        final String cookiesLine = "yummy_cookie=choco";
        final RequestCookie cookie = new RequestCookie();

        //when
        cookie.add(cookiesLine);

        //then
        assertThat(cookie.containsKey("yummy_cookie")).isTrue();
        assertThat(cookie.get("yummy_cookie")).isEqualTo("choco");
    }

    @DisplayName("요청 헤더 쿠키 파싱 테스트 - 쿠키가 2개일 때")
    @Test
    void parseTwoCookies() {
        //given
        final String cookiesLine = "yummy_cookie=choco; tasty_cookie=strawberry";
        final RequestCookie cookie = new RequestCookie();

        //when
        cookie.add(cookiesLine);

        //then
        assertThat(cookie.containsKey("yummy_cookie")).isTrue();
        assertThat(cookie.get("yummy_cookie")).isEqualTo("choco");

        assertThat(cookie.containsKey("tasty_cookie")).isTrue();
        assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry");
    }
}