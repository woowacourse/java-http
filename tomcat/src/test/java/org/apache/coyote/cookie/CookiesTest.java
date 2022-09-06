package org.apache.coyote.cookie;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookiesTest {

    @DisplayName("여러 쿠키값을 담은 문자열을 통해서 쿠키들을 생성할 수 있다.")
    @Test
    void makeCookies() {
        //given
        final Cookies cookies = Cookies.from("cookie1=one; cookie2=two");

        //when
        final Cookie cookie1 = cookies.getCookie("cookie1").orElseThrow();
        final Cookie cookie2 = cookies.getCookie("cookie2").orElseThrow();

        //then
        assertThat(cookie1.toHeaderFormat()).isEqualTo("cookie1=one");
        assertThat(cookie2.toHeaderFormat()).isEqualTo("cookie2=two");
    }

    @DisplayName("쿠키들 중 JSESSIONID 쿠키를 찾을 수 있다.")
    @Test
    void getJSessionCookie() {
        //given
        final Cookies cookies = Cookies.from("cookie1=one; cookie2=two; JSESSIONID=jsessionCookie");

        //when
        final Cookie jSessionCookie = cookies.getJSessionCookie().orElseThrow();

        //then
        assertThat(jSessionCookie.isJSessionCookie()).isTrue();
    }
}
