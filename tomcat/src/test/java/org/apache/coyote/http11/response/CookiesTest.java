package org.apache.coyote.http11.response;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

class CookiesTest {

    @Test
    @DisplayName("쿠키 헤더 값이 null이면 빈 맵으로 생성된다")
    void construct_null() {
        //given
        final String cookieHeader = null;

        //when
        final Cookies cookies = Cookies.from(cookieHeader);

        //then
        assertThat(cookies.notExist()).isTrue();
    }

    @Test
    @DisplayName("쿠키 헤더가 존재하면 파싱해서 생성한다")
    void construct_exist() {
        //given
        final String cookieHeader = "Cookie: JSESSIONID=test";

        //when
        final Cookies cookies = Cookies.from(cookieHeader);

        //then
        assertSoftly(softAssertions -> {
            assertThat(cookies.getCookieValue()).isEqualTo("test");
            assertThat(cookies.notExist()).isFalse();
        });
    }

    @Test
    @DisplayName("set-cookie 헤더 값을 만들 수 있다")
    void createNewCookieHeader() {
        //given
        final Cookies cookies = Cookies.from(null);

        //when
        final String cookieHeader = cookies.createNewCookieHeader();

        //then
        assertThat(cookieHeader).contains("Set-Cookie");
    }
}
