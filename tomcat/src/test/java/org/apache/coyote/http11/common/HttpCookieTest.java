package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import java.util.UUID;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpCookieTest {

    @Test
    void cookie_문자열을_입력받아_HttpCookie를_반환한다() {
        // given
        final String cookie = "yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        final HttpCookie httpCookie = HttpCookie.from(cookie);

        // then
        assertThat(httpCookie.getItems()).contains(
                entry("yummy_cookie", "choco"),
                entry("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @Test
    void key와_value를_입력받아_저장한다() {
        // given
        final HttpCookie httpCookie = new HttpCookie();

        // when
        httpCookie.put("hello", "world");

        // then
        assertThat(httpCookie.get("hello")).isEqualTo("world");
    }

    @Test
    void key를_입력받아_값을_반환한다() {
        // given
        final String uuid = UUID.randomUUID().toString();
        final String body = "JSESSIONID=" + uuid;
        final HttpCookie cookie = HttpCookie.from(body);

        // expect
        assertThat(cookie.get("JSESSIONID")).isEqualTo(uuid);
    }

    @Test
    void jSessionId를_가져온다() {
        // given
        final String uuid = UUID.randomUUID().toString();
        final String body = "JSESSIONID=" + uuid;
        final HttpCookie cookie = HttpCookie.from(body);

        // expect
        assertThat(cookie.getJSessionId()).isEqualTo(uuid);
    }
}
