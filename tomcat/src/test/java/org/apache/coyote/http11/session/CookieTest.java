package org.apache.coyote.http11.session;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieTest {

    @Test
    @DisplayName("요청 헤더의 쿠키 스트링을 쿠키 객체로 파싱한다")
    void parseFromRequestString() {
        String cookieString = "JSESSIONID=abc";

        Cookie cookie = Cookie.from(cookieString);

        assertAll(
                () -> assertThat(cookie.getName()).isEqualTo("JSESSIONID"),
                () -> assertThat(cookie.getValue()).isEqualTo("abc")
        );
    }

    @Test
    @DisplayName("쿠키 객체를 요청 응답 스트링으로 파싱한다")
    void parseFromCookie() {
        Cookie cookie = Cookie.from("JSESSIONID=abc");

        String headerValue = cookie.headerValue();

        assertThat(headerValue).isEqualTo("JSESSIONID=abc");
    }
}
