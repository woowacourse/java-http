package nextstep.jwp.http.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ResponseCookie 테스트")
class ResponseCookieTest {

    @DisplayName("응답 쿠키 헤더 변환 테스트 - 쿠키가 없을 때")
    @Test
    void toStringWhenCookieNotExists() {
        //given
        final ResponseCookie cookie = new ResponseCookie();

        //when
        final List<String> result = cookie.getAsSetCookieString();

        //then
        assertThat(result).isEmpty();
    }

    @DisplayName("응답 쿠키 헤더 변환 테스트 - 쿠키가 1개일 때")
    @Test
    void toStringWhenOneCookieExists() {
        //given
        final ResponseCookie cookie = new ResponseCookie();
        cookie.add("yummy_cookie", "choco");

        //when
        final List<String> result = cookie.getAsSetCookieString();

        //then
        assertThat(result).contains("Set-Cookie: yummy_cookie=choco ");
    }

    @DisplayName("응답 쿠키 헤더 변환 테스트 - 쿠키가 2개일 때")
    @Test
    void toStringWhenTwoCookiesExists() {
        //given
        final ResponseCookie cookie = new ResponseCookie();
        cookie.add("yummy_cookie", "choco");
        cookie.add("tasty_cookie", "strawberry");

        //when
        final List<String> result = cookie.getAsSetCookieString();

        //then
        assertThat(result)
                .contains("Set-Cookie: yummy_cookie=choco ")
                .contains("Set-Cookie: tasty_cookie=strawberry ");
    }
}