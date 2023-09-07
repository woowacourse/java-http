package org.apache.coyote.http11.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HeadersTest {

    @Test
    void 헤더값을_추가한다() {
        // given
        final Headers headers = new Headers();

        // when
        headers.addHeader("Connection: keep-alive");
        headers.addHeader("Sec-Fetch-Dest: image");

        // then
        assertThat(headers.getItems()).contains(
                entry("Connection", "keep-alive"),
                entry("Sec-Fetch-Dest", "image")
        );
    }

    @Test
    void 헤더값을_Key_Value_형식으로_추가한다() {
        // given
        final Headers headers = new Headers();

        // when
        headers.addHeader("Connection", "keep-alive");
        headers.addHeader("Sec-Fetch-Dest", "image");

        // then
        assertThat(headers.getItems()).contains(
                entry("Connection", "keep-alive"),
                entry("Sec-Fetch-Dest", "image")
        );
    }

    @Test
    void 쿠키값을_파싱하여_반환한다() {
        // given
        final Headers headers = new Headers();
        headers.addHeader("Cookie: yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");

        // when
        final HttpCookie httpCookie = headers.parseCookie();

        // then
        assertThat(httpCookie.getItems()).contains(
                entry("yummy_cookie", "choco"),
                entry("JSESSIONID", "656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }
}
