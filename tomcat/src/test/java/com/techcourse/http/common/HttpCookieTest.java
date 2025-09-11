package com.techcourse.http.common;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.http.HttpCookie;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @DisplayName("문자열로 쿠키 생성")
    @Test
    void fromTest1() {
        // given
        String cookieString = "JSESSIONID=abc123; theme=dark";

        // when
        HttpCookie httpCookie = HttpCookie.from(cookieString);

        // then
        assertThat(httpCookie.getJSessionId()).isEqualTo("abc123");
        assertThat(httpCookie.isEmpty()).isFalse();
    }

    @DisplayName("빈 문자열로 쿠키 생성")
    @Test
    void fromTest2() {
        // when
        HttpCookie httpCookie = HttpCookie.from("");

        // then
        assertThat(httpCookie.isEmpty()).isTrue();
        assertThat(httpCookie.getJSessionId()).isNull();
    }

    @DisplayName("잘못된 쿠키 형식인 경우")
    @Test
    void fromTest3() {
        // given
        String invalidCookieString = "invalid-cookie-format";

        // when & then
        assertThatThrownBy(() -> HttpCookie.from(invalidCookieString))
                .isInstanceOf(UncheckedServletException.class)
                .hasMessage("쿠키의 형식은 'key=value' 이여야 합니다.");
    }

    @DisplayName("빈 쿠키 생성")
    @Test
    void emptyTest() {
        // when
        HttpCookie httpCookie = HttpCookie.empty();

        // then
        assertThat(httpCookie.isEmpty()).isTrue();
        assertThat(httpCookie.getJSessionId()).isNull();
    }

    @DisplayName("세션 ID가 없을 때")
    @Test
    void hasEmptySessionIdTest1() {
        // given
        HttpCookie httpCookie = HttpCookie.from("theme=dark");

        // when & then
        assertThat(httpCookie.hasEmptySessionId()).isTrue();
    }

    @DisplayName("세션 ID가 있을 때")
    @Test
    void hasEmptySessionIdTest2() {
        // given
        HttpCookie httpCookie = HttpCookie.from("JSESSIONID=abc123");

        // when & then
        assertThat(httpCookie.hasEmptySessionId()).isFalse();
    }

    @DisplayName("세션 ID 추가")
    @Test
    void addSessionIdTest() {
        // given
        HttpCookie httpCookie = HttpCookie.empty();
        String sessionId = "new-session-id";

        // when
        httpCookie.addSessionId(sessionId);

        // then
        assertThat(httpCookie.getJSessionId()).isEqualTo(sessionId);
    }

    @DisplayName("http 헤더 형식으로 변환")
    @Test
    void toHttpHeaderFormatTest() {
        // given
        Map<String, String> values = new ConcurrentHashMap<>();
        values.put("JSESSIONID", "abc123");
        values.put("theme", "dark");
        HttpCookie httpCookie = new HttpCookie(values);

        // when
        String headerFormat = httpCookie.toHttpHeaderFormat();

        // then
        assertThat(headerFormat).isEqualTo("Set-Cookie: JSESSIONID=abc123; theme=dark ");
    }
}
