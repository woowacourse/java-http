package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import org.apache.catalina.session.Session;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("쿠키에 저장된 JSessionId를 검증한다.")
    void getCookieValue() {
        // given
        Session session = new Session("testId");
        HttpCookie cookie = HttpCookie.fromJSession(session);

        // when
        String actual = cookie.getCookieValue("JSESSIONID");

        // then
        assertThat(actual).isEqualTo("testId");
    }

    @Test
    @DisplayName("쿠키에 저장된 JSessionCookieHeader를 검증한다.")
    void getJSessionCookieHeader() {
        // given
        Session session = new Session("testId");
        HttpCookie cookie = HttpCookie.fromJSession(session);

        // when
        String actual = cookie.getJSessionCookieHeader();
        String expected = "JSESSIONID=testId";

        // then
        assertThat(actual).isEqualTo(expected);
    }
}