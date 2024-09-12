package org.apache.coyote.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

public class HttpCookieTest {

    @Test
    void 쿠키를_생성할_수_있다() {
        // given
        String cookieName = "username";
        String cookieValue = "JohnDoe";

        // when
        HttpCookie cookie = new HttpCookie(cookieName, cookieValue);

        // then
        assertThat(cookie.getName()).isEqualTo(cookieName);
        assertThat(cookie.getValue()).isEqualTo(cookieValue);
    }

    @Test
    void JSessionId_쿠키를_생성할_수_있다() {
        // given
        String sessionId = "abc123";

        // when
        HttpCookie jsessionCookie = HttpCookie.ofJSessionId(sessionId);

        // then
        assertThat(jsessionCookie.getName()).isEqualTo("JSESSIONID");
        assertThat(jsessionCookie.getValue()).isEqualTo(sessionId);
    }

    @Test
    void 쿠키의_toString_메서드가_올바르게_동작한다() {
        // given
        HttpCookie cookie = new HttpCookie("username", "JohnDoe");

        // when
        String result = cookie.toString();

        // then
        assertThat(result).isEqualTo("username=JohnDoe");
    }
}
