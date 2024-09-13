package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11CookieTest {

    @DisplayName("빈 쿠키 문자열을 처리할 때 빈 Http11Cookie 객체를 반환해야 한다.")
    @Test
    void isJSessionIdEmpty1() {
        Http11Cookie http11Cookie = Http11Cookie.from("");
        assertThat(http11Cookie).isNotNull();
        assertThat(http11Cookie.isJSessionIdEmpty()).isTrue();
    }

    @DisplayName("쿠키 문자열에서 JSESSIONID가 없으면 isJSessionIdEmpty()가 true를 반환해야 한다.")
    @Test
    void isJSessionIdEmpty2() {
        Http11Cookie http11Cookie = Http11Cookie.from("SESSIONID=12345;");
        assertThat(http11Cookie.isJSessionIdEmpty()).isTrue();
    }

    @DisplayName("쿠키 문자열에서 JSESSIONID가 있으면 isJSessionIdEmpty()가 false를 반환해야 한다.")
    @Test
    void isJSessionIdEmpty3() {
        Http11Cookie http11Cookie = Http11Cookie.from("JSESSIONID=abc123;");
        assertThat(http11Cookie.isJSessionIdEmpty()).isFalse();
    }

    @DisplayName("JSESSIONID가 포함된 쿠키 문자열에서 JSESSIONID 값을 올바르게 반환해야 한다.")
    @Test
    void getJSessionId1() {
        Http11Cookie http11Cookie = Http11Cookie.from("JSESSIONID=abc123;");
        assertThat(http11Cookie.getJSessionId()).isEqualTo("abc123");
    }

    @DisplayName("JSESSIONID가 포함되지 않은 쿠키 문자열에서 JSESSIONID를 요청할 경우 null을 반환해야 한다.")
    @Test
    void getJSessionId2() {
        Http11Cookie http11Cookie = Http11Cookie.from("SESSIONID=12345;");
        assertThat(http11Cookie.getJSessionId()).isNull();
    }

    @DisplayName("여러 쿠키가 포함된 문자열에서 JSESSIONID를 올바르게 반환해야 한다.")
    @Test
    void getJSessionId3() {
        Http11Cookie http11Cookie = Http11Cookie.from("SESSIONID=12345; JSESSIONID=abc123;");
        assertThat(http11Cookie.getJSessionId()).isEqualTo("abc123");
    }
}
