package org.apache.coyote.http11.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class HttpCookieTest {
    @Test
    void 여러_쿠키를_파싱한다() {
        final HttpCookie cookie = HttpCookie.from("a=1; b=2");

        assertThat(cookie.get("a")).hasValue("1");
        assertThat(cookie.get("b")).hasValue("2");
    }

    @Test
    void 쿠키_이름은_대소문자를_구분한다() {
        final HttpCookie cookie = HttpCookie.from("JSESSIONID=abc");

        assertThat(cookie.get("JSESSIONID")).hasValue("abc");
        assertThat(cookie.get("jsessionid")).isEmpty();
    }

    @Test
    void 같은_이름이면_첫_번째를_쓴다() {
        final HttpCookie cookie = HttpCookie.from("id=specific; id=general");

        assertThat(cookie.get("id")).hasValue("specific");
    }

    @Test
    void 값에_등호가_있어도_첫_등호에서만_나눈다() {
        final HttpCookie cookie = HttpCookie.from("token=abc==");

        assertThat(cookie.get("token")).hasValue("abc==");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", ";", ";;", "noequals", "=abc"})
    void 의미_없는_조각은_무시한다(final String raw) {
        final HttpCookie cookie = HttpCookie.from(raw);

        assertThat(cookie.get("")).isEmpty();
        assertThat(cookie.get("noequals")).isEmpty();
    }
}