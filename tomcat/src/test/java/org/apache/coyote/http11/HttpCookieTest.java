package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("쿠키를 생성한다.")
    @Test
    void from() {
        // given
        final String rawCookie = "JSESSIONID=1234; TEMP=aaaa";

        // when
        final HttpCookie cookie = HttpCookie.from(rawCookie);

        // then
        assertAll(
            () -> assertThat(cookie.getJSessionID()).isEqualTo("1234"),
            () -> assertThat(cookie.getValues().get("TEMP")).isEqualTo("aaaa")
        );
    }

    @DisplayName("JSESSIONID 를 조회할 때, 존재하지 않으면 예외를 반환한다.")
    @Test
    void getJSessionIDThrowsException_NotExistJSessionID() {
        // given
        final String rawCookie = "TEMP=aaaa";
        final HttpCookie cookie = HttpCookie.from(rawCookie);

        // when
        // then
        assertThatThrownBy(cookie::getJSessionID)
            .isInstanceOf(IllegalStateException.class);
    }

    @DisplayName("쿠키를 문자열로 변환한다.")
    @Test
    void cookieToString() {
        // given
        final String rawCookie = "JSESSIONID=1234; TEMP=aaaa";
        final HttpCookie cookie = HttpCookie.from(rawCookie);

        // when
        // then
        assertThat(cookie.toString()).isEqualTo("JSESSIONID=1234; TEMP=aaaa");
    }
}
