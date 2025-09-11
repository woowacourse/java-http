package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.net.HttpCookie;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @DisplayName("이름으로 쿠키를 찾을 수 있다.")
    @Test
    void getCookie_existing() {
        // given
        final var jsessionid = new HttpCookie("JSESSIONID", "1234abcd");
        final var lang = new HttpCookie("lang", "ko");
        final var httpCookies = new HttpCookies(List.of(jsessionid, lang));

        // when
        final var foundCookie = httpCookies.getCookie("JSESSIONID");

        // then
        assertAll(
                () -> assertThat(foundCookie).isPresent(),
                () -> assertThat(foundCookie.get().getValue()).isEqualTo("1234abcd")
        );
    }

    @DisplayName("존재하지 않는 이름의 쿠키를 찾으면 빈 Optional을 반환한다.")
    @Test
    void getCookie_non_existing() {
        // given
        final var lang = new HttpCookie("lang", "ko");
        final var httpCookies = new HttpCookies(List.of(lang));

        // when
        final var foundCookie = httpCookies.getCookie("JSESSIONID");

        // then
        assertThat(foundCookie).isEmpty();
    }

    @DisplayName("쿠키가 없으면 isEmpty가 true를 반환한다.")
    @Test
    void isEmpty_true() {
        // given
        final var httpCookies = new HttpCookies(List.of());

        // when
        final boolean isEmpty = httpCookies.isEmpty();

        // then
        assertThat(isEmpty).isTrue();
    }

    @DisplayName("쿠키가 있으면 isEmpty가 false를 반환한다.")
    @Test
    void isEmpty_false() {
        // given
        final var jsessionid = new HttpCookie("JSESSIONID", "1234abcd");
        final var httpCookies = new HttpCookies(List.of(jsessionid));

        // when
        final boolean isEmpty = httpCookies.isEmpty();

        // then
        assertThat(isEmpty).isFalse();
    }
}
