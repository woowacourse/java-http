package org.apache.coyote.http.cookie;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class HttpCookiesTest {

    @Test
    @DisplayName("유효한 쿠키 헤더를 받아와 HttpCookie를 생성한다.")
    void from_success() {
        //given
        String cookieHeader = "yummy_cookie=choco; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46";

        //when, then
        assertAll(
                () -> assertDoesNotThrow(() -> HttpCookies.from(cookieHeader)),
                () -> assertThat(HttpCookies.from(cookieHeader).getJsessionid()).isEqualTo("656cef62-e3c4-40bc-a8df-94732920ed46")
        );
    }

    @NullAndEmptySource
    @ParameterizedTest
    @DisplayName("빈 쿠키 헤더가 들어왔을 때 예외를 반환한다.")
    void from_fail_InvalidCookieHeader(String cookieHeader) {
        //when, then
        assertThatThrownBy(() -> HttpCookies.from(cookieHeader))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("쿠키 헤더");
    }
}
