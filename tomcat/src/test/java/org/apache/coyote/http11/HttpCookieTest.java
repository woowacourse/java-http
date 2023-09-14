package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("문자열 형태로 전달된 쿠키를 생성할 수 있다 - 1개")
    void make_cookie_1() {
        String cookieQuery = "yummy_cookie=choco";
        HttpCookie httpCookie = HttpCookie.from(cookieQuery);

        assertThat(httpCookie.getValue("yummy_cookie")).isEqualTo("choco");
    }

    @Test
    @DisplayName("문자열 형태로 전달된 쿠키를 생성할 수 있다 - 2개 이상")
    void make_cookie_2() {
        String cookieQuery = "yummy_cookie=choco;JSESSIONID=123123";
        HttpCookie httpCookie = HttpCookie.from(cookieQuery);

        assertAll(
                () -> assertThat(httpCookie.getValue("yummy_cookie")).isEqualTo("choco"),
                () -> assertThat(httpCookie.getValue("JSESSIONID")).isEqualTo("123123")
        );
    }

    @Test
    @DisplayName("문자열 형태로 전달된 쿠키를 생성할 수 있다 - 0개")
    void make_cookie_0() {
        String cookieQuery = "";
        HttpCookie httpCookie = HttpCookie.from(cookieQuery);

        assertThat(httpCookie.isEmpty()).isTrue();
    }


    @Test
    @DisplayName("생성된 쿠키를 다시 문자열형태로 돌릴 수 있다.")
    void get_cookie() {
        String cookieQuery = "yummy_cookie=choco;JSESSIONID=123123";
        HttpCookie httpCookie = HttpCookie.from(cookieQuery);

        assertThat(httpCookie.getCookies()).isEqualTo(cookieQuery);
    }
}
