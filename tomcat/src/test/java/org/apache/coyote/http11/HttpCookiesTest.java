package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @DisplayName("쿠키를 추가할 수 있다.")
    @Test
    void addCookie() {
        // given
        String cookieName = "JSESSIONID";
        String cookieValue = "someValue";

        // when
        HttpCookies cookies = new HttpCookies();
        cookies.addCookie(cookieName, cookieValue);

        // then
        assertThat(cookies.getCookieValue(cookieName)).isEqualTo(cookieValue);
    }

    @DisplayName("http request 값에서 쿠키를 추출할 수 있다.")
    @Test
    void parseCookie() {
        // given
        String rawCookies = "JSESSIONID=123456; name=value";

        // when
        HttpCookies cookies = new HttpCookies(rawCookies);
        // then
        assertThat(cookies.getCookieValue("JSESSIONID")).isEqualTo("123456");
    }

    @DisplayName("생성한 쿠키 값을 전달할 수 있도록 변환한다.")
    @Test
    void buildOutput() {
        // given
        String cookieName = "JSESSIONID";
        String cookieValue = "someValue";

        // when
        HttpCookies cookies = new HttpCookies();
        cookies.addCookie(cookieName, cookieValue);

        // then
        assertThat(cookies.buildOutput()).isEqualTo("JSESSIONID=someValue");
    }
}