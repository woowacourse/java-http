package org.apache.coyote.http11.cookie;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class HttpCookiesTest {

    @Test
    void header의_정보로_쿠키를_만든다() {
        // given
        String header = "JSESSIONID=1234; Path=/;";

        // when
        HttpCookies httpCookies = HttpCookies.parse(header);

        // then
        assertThat(httpCookies.getCookieValue("JSESSIONID")).isEqualTo("1234");
        assertThat(httpCookies.getCookieValue("Path")).isEqualTo("/");
    }

    @Test
    void 쿠키에_세션이_존재하면_true를_반환한다() {
        // given
        String header = "JSESSIONID=1234; Path=/;";

        // when
        HttpCookies httpCookies = HttpCookies.parse(header);

        // then
        assertThat(httpCookies.existsSession()).isTrue();
    }
}
