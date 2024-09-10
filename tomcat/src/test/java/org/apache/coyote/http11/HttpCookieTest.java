package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {
    @Test
    @DisplayName("유효한 쿠키 헤더로 HttpCookie를 생성한다.")
    void createHttpCookie() {
        // given
        String cookieHeader = "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46; yummy_cookie=choco; tasty_cookie=strawberry";

        // when
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        // then
        assertAll(
                () -> assertThat(httpCookie.hasJSessionId()).isTrue(),
                () -> assertThat(httpCookie.getJsessionid()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @Test
    @DisplayName("JSESSIONID 쿠키 문자열을 생성한다.")
    void ofJSessionId() {
        // given
        String value = "12345";

        // when
        String result = HttpCookie.ofJSessionId(value);

        // then
        assertThat(result).isEqualTo("JSESSIONID=12345");
    }

    @Test
    @DisplayName("JSESSIONID가 없는 경우 hasJSessionId의 결과 false를 반환한다.")
    void doesNotHaveJSessionId() {
        // given
        String cookieHeader = "yummy_cookie=choco";

        // when
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        // then
        assertThat(httpCookie.hasJSessionId()).isFalse();
    }

    @Test
    @DisplayName("JSESSIONID가 없는 경우 getJsessionid은 null을 반환한다.")
    void cannotGetJsessionid() {
        // given
        String cookieHeader = "yummy_cookie=choco";

        // when
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        // then
        assertThat(httpCookie.getJsessionid()).isNull();
    }

    @Test
    @DisplayName("JSESSIONID가 없는 경우 getJsessionid 테스트")
    void getJsessionid() {
        // given
        String cookieHeader = "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        // then
        assertThat(httpCookie.getJsessionid()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }
}
