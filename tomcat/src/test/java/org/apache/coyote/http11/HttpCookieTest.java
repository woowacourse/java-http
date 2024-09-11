package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("쿠키 문자열을 파싱하여 인스턴스 생성")
    @Test
    void construct_Success() {
        HttpCookie httpCookie = new HttpCookie("name1=value1; name2=value2");
        assertThat(httpCookie.buildMessage())
                .isIn("name1=value1; name2=value2", "name2=value2; name1=value1");
    }

    @DisplayName("쿠키에 세션 추가")
    @Test
    void setSession() {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.setSession("value1");
        assertThat(httpCookie.buildMessage()).isEqualTo("JSESSIONID=value1");
    }

    @DisplayName("쿠키에 세션이 포함되어 있는지 여부 정상 반환")
    @Test
    void hasSession() {
        // given
        HttpCookie cookieWithSession = new HttpCookie("JSESSIONID=test-session; tasty_cookie=strawberry");
        HttpCookie cookieWithoutSession = new HttpCookie("yummy_cookie=choco; tasty_cookie=strawberry");
        HttpCookie blankCookie = new HttpCookie();

        // when & then
        assertAll(
                () -> assertThat(cookieWithSession.hasSession()).isTrue(),
                () -> assertThat(cookieWithoutSession.hasSession()).isFalse(),
                () -> assertThat(blankCookie.hasSession()).isFalse()
        );
    }

    @DisplayName("쿠키에서 세션 조회")
    @Test
    void getSession() {
        // given
        HttpCookie cookie = HttpCookie.ofSessionId("testsession");

        // when & then
        assertThat(cookie.getSession()).isEqualTo("testsession");
    }

    @DisplayName("쿠키를 String으로 변환")
    @Test
    void buildMessage() {
        // given
        HttpCookie cookie = new HttpCookie("yummy_cookie=choco");
        cookie.setSession("testsession");

        // when
        assertThat(cookie.buildMessage()).containsAnyOf(
                "yummy_cookie=choco; JSESSIONID=testsession",
                "JSESSIONID=testsession; yummy_cookie=choco"
        );
    }
}
