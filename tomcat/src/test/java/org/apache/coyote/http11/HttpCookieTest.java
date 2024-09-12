package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.techcourse.exception.UncheckedServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class HttpCookieTest {

    @DisplayName("단일 쿠키 생성")
    @Test
    void construct_Success_One() {
        // given & when
        HttpCookie httpCookie = new HttpCookie("name1=value1");

        // then
        assertThat(httpCookie.buildMessage()).isEqualTo("name1=value1");
    }

    @DisplayName("쿠키 문자열을 파싱하여 인스턴스 생성")
    @ParameterizedTest
    @ValueSource(strings = {"name1=value1; name2=value2", "name1=value1;name2=value2", "name1=value1 ; name2=value2"})
    void construct_Success(String rawCookies) {
        // given & when
        HttpCookie httpCookie = new HttpCookie(rawCookies);

        // then
        assertThat(httpCookie.buildMessage())
                .isIn("name1=value1; name2=value2", "name2=value2; name1=value1");
    }

    @DisplayName("쿠키 파싱 실패: 올바르지 않은 형식")
    @ParameterizedTest
    @ValueSource(strings = {
            "name1value1",
            ";name1=value1;name2=value2",
            "name1=value1;name2=value2;",
            "name1=value1; name2value2"
    })
    @EmptySource
    void construct_Fail_InvalidFormat(String rawCookies) {
        assertThatThrownBy(() -> new HttpCookie(rawCookies))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("형식이 올바르지 않은 쿠키가 포함되어 있습니다.");
    }

    @DisplayName("쿠키 파싱 실패: 중복된 쿠키 이름")
    @Test
    void construct_Fail_DuplicatedCookieNames() {
        assertThatThrownBy(() -> new HttpCookie("a=a; a=b"))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("쿠키의 이름은 중복될 수 없습니다.");
    }

    @DisplayName("쿠키에 세션 추가")
    @Test
    void setSession() {
        // given
        HttpCookie httpCookie = new HttpCookie();

        // when
        httpCookie.setSession("value1");

        // then
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
