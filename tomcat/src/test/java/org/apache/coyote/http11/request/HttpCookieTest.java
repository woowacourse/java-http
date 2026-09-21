package org.apache.coyote.http11.request;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpCookieTest {
    @Test
    void parseCookies() {
        // when
        final HttpCookie cookie = HttpCookie.from(
                "yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        // then
        assertThat(cookie.get("yummy_cookie")).isEqualTo("choco");
        assertThat(cookie.get("tasty_cookie")).isEqualTo("strawberry");
        assertThat(cookie.get("JSESSIONID")).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @Test
    void emptyWhenHeaderIsMissing() {
        // when
        final HttpCookie cookie = HttpCookie.from(null);

        // then
        assertThat(cookie.get("JSESSIONID")).isNull();
    }

    @Test
    void valueCanContainEqualsSign() {
        // when
        final HttpCookie cookie = HttpCookie.from("token=abc==");

        // then
        assertThat(cookie.get("token")).isEqualTo("abc==");
    }

    @Test
    void ignoreMalformedCookie() {
        // when
        final HttpCookie cookie = HttpCookie.from("broken; =novalue; JSESSIONID=abc");

        // then
        assertThat(cookie.get("broken")).isNull();
        assertThat(cookie.get("JSESSIONID")).isEqualTo("abc");
    }

    @Test
    void firstValueWinsWhenNameIsDuplicated() {
        // when
        final HttpCookie cookie = HttpCookie.from("JSESSIONID=first; JSESSIONID=second");

        // then
        assertThat(cookie.get("JSESSIONID")).isEqualTo("first");
    }

    @Test
    void addCookies() {
        // given
        final HttpCookie cookie = HttpCookie.empty();

        // when
        cookie.add("JSESSIONID", "abc");
        cookie.add("theme", "dark");

        // then
        assertThat(cookie.toHeaderValues()).containsExactly("JSESSIONID=abc", "theme=dark");
    }

    @Test
    void addSameNameReplacesValue() {
        // given
        final HttpCookie cookie = HttpCookie.empty();

        // when
        cookie.add("JSESSIONID", "old");
        cookie.add("JSESSIONID", "new");

        // then
        assertThat(cookie.toHeaderValues()).containsExactly("JSESSIONID=new");
    }
}
