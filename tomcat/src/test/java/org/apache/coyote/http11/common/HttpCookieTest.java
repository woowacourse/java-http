package org.apache.coyote.http11.common;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpCookie 단위 테스트")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpCookieTest {

    @Test
    void 헤더의_쿠키_정보로부터_쿠키_객체_생성_테스트() {
        // given
        final String cookies =
                "yummy_cookie=choco; newjeans_cookie=newjeans; JSESSIONID=randomUUID";

        // when
        final HttpCookie httpCookie = HttpCookie.from(cookies);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(httpCookie.getCookieValue("yummy_cookie")).isEqualTo("choco");
            softly.assertThat(httpCookie.getCookieValue("newjeans_cookie")).isEqualTo("newjeans");
            softly.assertThat(httpCookie.getCookieValue("JSESSIONID")).isEqualTo("randomUUID");
        });
    }
}
