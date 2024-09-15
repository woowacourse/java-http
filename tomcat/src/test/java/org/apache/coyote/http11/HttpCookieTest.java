package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpCookieTest {
    @DisplayName("유효한 쿠키 헤더로 HttpCookie를 생성한다.")
    @Test
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

    @DisplayName("유효하지 않은 쿠키 헤더는 HttpCookie에 추가되지 않는다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void cannotCreateHttpCookie(String id) {
        // given
        String cookieHeader = "JSESSIONID=" + id + "; yummy_cookie=choco; tasty_cookie=strawberry";

        // when
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        // then
        assertThat(httpCookie.hasJSessionId()).isFalse();
    }

    @DisplayName("JSESSIONID 쿠키 문자열을 생성한다.")
    @Test
    void ofJSessionId() {
        // given
        String value = "12345";

        // when
        String result = HttpCookie.ofJSessionId(value);

        // then
        assertThat(result).isEqualTo("JSESSIONID=12345");
    }

    @DisplayName("JSESSIONID가 있는 경우 hasJsessionid는 참을 반환한다.")
    @Test
    void hasJsessionid() {
        // given
        String cookieHeader = "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        // then
        assertThat(httpCookie.hasJSessionId()).isTrue();
    }

    @DisplayName("JSESSIONID가 없는 경우 hasJSessionId의 결과 false를 반환한다.")
    @Test
    void doesNotHaveJSessionId() {
        // given
        String cookieHeader = "yummy_cookie=choco";

        // when
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        // then
        assertThat(httpCookie.hasJSessionId()).isFalse();
    }

    @DisplayName("JSESSIONID가 있는 경우 id를 반환한다.")
    @Test
    void getJsessionid() {
        // given
        String cookieHeader = "JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        // when
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        // then
        assertThat(httpCookie.getJsessionid()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46");
    }

    @DisplayName("JSESSIONID가 없는 경우 getJsessionid은 null을 반환한다.")
    @Test
    void cannotGetJsessionid() {
        // given
        String cookieHeader = "yummy_cookie=choco";

        // when
        HttpCookie httpCookie = new HttpCookie(cookieHeader);

        // then
        assertThat(httpCookie.getJsessionid()).isNull();
    }
}
