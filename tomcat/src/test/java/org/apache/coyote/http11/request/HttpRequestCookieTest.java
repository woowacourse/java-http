package org.apache.coyote.http11.request;

import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayName("HttpRequestCookie 단위 테스트")
class HttpRequestCookieTest {

    @Test
    void 헤더의_쿠키_정보로부터_쿠키_객체_생성_테스트() {
        // given
        final String cookies =
                "yummy_cookie=choco; newjeans_cookie=newjeans; JSESSIONID=randomUUID";

        // when
        final HttpRequestCookie httpRequestCookie = HttpRequestCookie.from(cookies);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(httpRequestCookie.getCookieValue("yummy_cookie")).isEqualTo("choco");
            softly.assertThat(httpRequestCookie.getCookieValue("newjeans_cookie")).isEqualTo("newjeans");
            softly.assertThat(httpRequestCookie.getCookieValue("JSESSIONID")).isEqualTo("randomUUID");
        });
    }
}
