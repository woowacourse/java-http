package org.apache.coyote.http.request;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpCookieTest {

    @Test
    void 쿠키를_생성한다() {
        HttpCookie cookie = HttpCookie.from("JSESSIONID=ABCDE; theme=dark");

        assertThat(cookie.get("JSESSIONID")).contains("ABCDE");
        assertThat(cookie.get("theme")).contains("dark");
    }

    @Test
    void 값에_포함된_등호는_값의_일부로_유지한다() {
        HttpCookie cookie = HttpCookie.from("JSESSIONID=ABCDE==123");

        assertThat(cookie.get("JSESSIONID")).contains("ABCDE==123");
    }

    @Test
    void 존재하지_않는_쿠키는_빈_값을_반환한다() {
        HttpCookie cookie = HttpCookie.from("JSESSIONID=ABCDE");

        assertThat(cookie.get("theme")).isEmpty();
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {"   "})
    void 쿠키_헤더_값이_비어있으면_빈_쿠키를_생성한다(String rawCookie) {
        HttpCookie cookie = HttpCookie.from(rawCookie);

        assertThat(cookie.get("JSESSIONID")).isEmpty();
    }
}
