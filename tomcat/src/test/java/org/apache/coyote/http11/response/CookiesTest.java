package org.apache.coyote.http11.response;

import org.apache.coyote.http11.request.header.Cookies;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CookiesTest {

    @Test
    @DisplayName("쿠키 헤더 값이 null이면 빈 맵으로 생성된다")
    void construct_null() {
        //given
        final String cookieHeader = null;

        //when
        final Cookies cookies = Cookies.from(cookieHeader);

        //then
        assertThat(cookies.getJavaSessionId()).isNull();
    }

    @Test
    @DisplayName("쿠키 헤더가 존재하면 파싱해서 생성한다")
    void construct_exist() {
        //given
        final String cookieHeader = "JSESSIONID=test";

        //when
        final Cookies cookies = Cookies.from(cookieHeader);

        //then
        assertThat(cookies.getJavaSessionId()).isEqualTo("test");
    }

    @Test
    @DisplayName("set-cookie 헤더 값을 만들 수 있다")
    void createNewCookieHeader() {
        //given
        final Cookies cookies = Cookies.from(null);

        //when
        final String cookieHeader = cookies.createNewSession();

        //then
        assertThat(cookieHeader).contains("JSESSIONID=");
    }
}
