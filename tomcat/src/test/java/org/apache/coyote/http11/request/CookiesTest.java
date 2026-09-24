package org.apache.coyote.http11.request;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CookiesTest {

    @Test
    @DisplayName("쿠키 헤더가 null이면 빈 쿠키 목록을 생성한다.")
    void createEmptyCookiesWhenHeaderIsNull() {
        // when
        Cookies cookies = Cookies.from(null);

        // then
        assertThat(cookies.find(Cookie.JSESSIONID)).isEmpty();
    }

    @Test
    @DisplayName("쿠키 헤더가 비어 있으면 빈 쿠키 목록을 생성한다.")
    void createEmptyCookiesWhenHeaderIsBlank() {
        // when
        Cookies cookies = Cookies.from("");

        // then
        assertThat(cookies.find(Cookie.JSESSIONID)).isEmpty();
    }

    @Test
    @DisplayName("쿠키 헤더에서 JSESSIONID 값을 찾는다.")
    void findJSessionId() {
        // when
        Cookies cookies = Cookies.from("yummy_cookie=choco; JSESSIONID=session-id");

        // then
        assertThat(cookies.find(Cookie.JSESSIONID))
                .map(Cookie::getValue)
                .contains("session-id");
    }
}
