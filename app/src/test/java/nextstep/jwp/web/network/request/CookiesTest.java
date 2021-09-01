package nextstep.jwp.web.network.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class CookiesTest {

    @DisplayName("String으로 Cookies 객체를 생성한다 - 성공")
    @Test
    void create() {
        // given
        final String cookieAsString = "sweetCookie=sugar; sourCookie=medicine";

        // when // then
        assertThatCode(() -> Cookies.of(cookieAsString))
                .doesNotThrowAnyException();
    }

    @DisplayName("Cookies 안에서 원하는 쿠키 값을 가져온다 - 성공")
    @Test
    void getCookieValue() {
        // given
        final Cookies cookies = Cookies.of("sweetCookie=sugar; sourCookie=medicine");

        // when
        final String actual = cookies.get("sweetCookie");

        // then
        assertThat(actual).isEqualTo("sugar");
    }

    @DisplayName("Cookie 안에 찾는 쿠키 값이 없을 경우 null을 반환한다 - 성공")
    @Test
    void getNullCookieValue() {
        // given
        final Cookies cookies = Cookies.of("sweetCookie=sugar; sourCookie=medicine");

        // when
        final String actual = cookies.get("spicyCookie");

        // then
        assertThat(actual).isNull();
    }
}