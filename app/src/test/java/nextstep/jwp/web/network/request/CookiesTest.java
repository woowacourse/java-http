package nextstep.jwp.web.network.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

class CookiesTest {

    @DisplayName("String으로 Cookies 객체를 생성한다 - 성공")
    @Test
    void create() {
        // given
        final String cookieAsString = "sweetCookie=sugar; sourCookie=medicine";

        // when // then
        assertThatCode(() -> new Cookies(cookieAsString))
                .doesNotThrowAnyException();
    }

    @Test
    void getCookiesAsList() {
        // given

        // when

        // then
    }
}