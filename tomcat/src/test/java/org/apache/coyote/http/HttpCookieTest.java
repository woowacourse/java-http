package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.http.exception.InvalidCookieContentException;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class HttpCookieTest {

    @Test
    void fromSessionId_메서드는_sessionId를_전달하면_HttpCookie를_생성한다() {
        final HttpCookie actual = HttpCookie.fromSessionId("abcde");

        assertThat(actual).isNotNull();
    }

    @Test
    void fromCookieHeaderValue_메서드는_쿠키_헤더를_전달하면_HttpCookie를_생성한다() {
        final String cookieHeaderValue = "JSESSIONID=abcde; cherry=pick;";

        final HttpCookie actual = HttpCookie.fromCookieHeaderValue(cookieHeaderValue);

        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).isNotNull();
            softAssertions.assertThat(actual.findValue("JSESSIONID")).isEqualTo("abcde");
            softAssertions.assertThat(actual.findValue("cherry")).isEqualTo("pick");
        });
    }

    @Test
    void fromCookieHeaderValue_메서드는_null을_전달하면_HttpCookie를_생성한다() {
        assertThatThrownBy(() -> HttpCookie.fromCookieHeaderValue(null))
                .isInstanceOf(InvalidCookieContentException.class)
                .hasMessageContaining("허용되지 않는 쿠기 값입니다.");
    }

    @Test
    void findValue_메서드는_key를_전달하면_value를_반환한다() {
        final String sessionId = "abcde";
        final HttpCookie cookie = HttpCookie.fromSessionId(sessionId);

        final String actual = cookie.findValue("JSESSIONID");

        assertThat(actual).isEqualTo(sessionId);
    }

    @Test
    void convertHeaders_메서드는_쿠키의_헤더를_문자열로_변환해_반환한다() {
        final HttpCookie cookie = HttpCookie.fromSessionId("abcde");

        final String actual = cookie.convertHeaders();

        assertThat(actual).contains("Set-Cookie: JSESSIONID=abcde;");
    }
}
