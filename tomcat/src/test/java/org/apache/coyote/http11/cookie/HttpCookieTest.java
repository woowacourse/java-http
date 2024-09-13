package org.apache.coyote.http11.cookie;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import org.apache.coyote.http11.exception.HttpFormatException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpCookieTest {

    @DisplayName("문자열을 파싱하여 쿠키들을 생성한다.")
    @Test
    void createCookie() {
        String cookies = "potato=me;onion=you";

        List<HttpCookie> httpCookies = HttpCookie.createCookies(cookies);

        assertThat(httpCookies)
                .extracting(HttpCookie::name, HttpCookie::value)
                .contains(Tuple.tuple("potato", "me"))
                .contains(Tuple.tuple("onion", "you"));
    }

    @DisplayName("잘못된 쿠키 형식일 경우 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(strings = {"potato=me;onion", "potato:me;onion:you", "potato=;onion=you"})
    void invalidCookie(String cookies) {

        assertThatThrownBy(() -> HttpCookie.createCookies(cookies))
                .isInstanceOf(HttpFormatException.class)
                .hasMessage("올바르지 않은 쿠키 형식입니다.");
    }

    @DisplayName("세션 쿠키를 생성한다.")
    @Test
    void createSessionCookie() {
        String value = "123431";
        HttpCookie cookie = HttpCookie.ofSession(value);

        assertThat(cookie).isEqualTo(new HttpCookie("JSESSIONID", value));
    }
}
