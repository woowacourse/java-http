package org.apache.catalina.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.spring.http.cookie.HttpCookie;
import com.spring.http.cookie.HttpCookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookiesTest {

    @DisplayName("from 메서드: 쿠키 헤더 문자열을 파싱하여 HttpCookies 객체를 생성한다")
    @Test
    void from_parseCookieHeaderTest1() {
        // given
        String cookieHeader = "sessionId=abc123; userId=456; theme=dark";

        // when
        HttpCookies cookies = HttpCookies.from(cookieHeader);

        // then
        assertThat(cookies.getCookie("sessionId").value()).isEqualTo("abc123");
        assertThat(cookies.getCookie("userId").value()).isEqualTo("456");
        assertThat(cookies.getCookie("theme").value()).isEqualTo("dark");
    }

    @DisplayName("from 메서드: ;가 붙어도 정상적으로 파싱한다")
    @Test
    void from_parseCookieHeaderTest2() {
        // given
        String cookieHeader = "sessionId=abc123;userId=456;theme=dark";

        // when
        HttpCookies cookies = HttpCookies.from(cookieHeader);

        // then
        assertThat(cookies.getCookie("sessionId").value()).isEqualTo("abc123");
        assertThat(cookies.getCookie("userId").value()).isEqualTo("456");
        assertThat(cookies.getCookie("theme").value()).isEqualTo("dark");
    }

    @DisplayName("from 메서드: 단일 쿠키를 파싱한다")
    @Test
    void from_singleCookie() {
        // given
        String cookieHeader = "sessionId=abc123";

        // when
        HttpCookies cookies = HttpCookies.from(cookieHeader);

        // then
        assertThat(cookies.getCookie("sessionId").value()).isEqualTo("abc123");
    }

    @DisplayName("from 메서드: 빈 문자열일 때 빈 HttpCookies를 반환한다")
    @Test
    void from_emptyString() {
        // given
        String cookieHeader = "";

        // when
        HttpCookies cookies = HttpCookies.from(cookieHeader);

        // then
        assertThat(cookies.toString()).isEmpty();
    }

    @DisplayName("from 메서드: null일 때 빈 HttpCookies를 반환한다")
    @Test
    void from_null() {
        // when
        HttpCookies cookies = HttpCookies.from(null);

        // then
        assertThat(cookies.toString()).isEmpty();
    }

    @DisplayName("from 메서드: 값이 없는 쿠키를 처리한다")
    @Test
    void from_cookieWithoutValue() {
        // given
        String cookieHeader = "sessionId=abc123; emptyValue=; noValue";

        // when
        HttpCookies cookies = HttpCookies.from(cookieHeader);

        // then
        assertThat(cookies.getCookie("sessionId").value()).isEqualTo("abc123");
        assertThat(cookies.getCookie("emptyValue").value()).isEmpty();
        assertThat(cookies.getCookie("noValue").value()).isEmpty();
    }

    @DisplayName("toString 메서드: HttpCookies를 쿠키 헤더 형식 문자열로 변환한다")
    @Test
    void toString_convertToHeaderString() {
        // given
        HttpCookies cookies = new HttpCookies();
        cookies.addCookie(new HttpCookie("sessionId", "abc123"));
        cookies.addCookie(new HttpCookie("userId", "456"));

        // when
        String result = cookies.toString();

        // then
        assertThat(result).isEqualTo("sessionId=abc123; userId=456");
    }

    @DisplayName("toString 메서드: 빈 HttpCookies는 빈 문자열을 반환한다")
    @Test
    void toString_emptyCookies() {
        // given
        HttpCookies cookies = new HttpCookies();

        // when
        String result = cookies.toString();

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("hasCookie 메서드: 쿠키가 존재하는지 확인한다")
    @Test
    void hasCookie_existingCookie() {
        // given
        HttpCookies cookies = HttpCookies.from("sessionId=abc123; userId=456");

        // when & then
        assertThat(cookies.hasCookie("sessionId")).isTrue();
        assertThat(cookies.hasCookie("userId")).isTrue();
        assertThat(cookies.hasCookie("nonExistent")).isFalse();
    }

    @DisplayName("addCookie 메서드: 새 쿠키를 추가한다")
    @Test
    void addCookie_newCookie() {
        // given
        HttpCookies cookies = new HttpCookies();

        // when
        cookies.addCookie(new HttpCookie("newCookie", "newValue"));

        // then
        assertThat(cookies.hasCookie("newCookie")).isTrue();
        assertThat(cookies.getCookie("newCookie").value()).isEqualTo("newValue");
    }
}
