package org.apache.coyote.cookie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    private static final String JSESSIONID = "656cef62-e3c4-40bc-a8df-94732920ed46";
    private static final String JSESSIONID_COOKIE = "JSESSIONID=" + JSESSIONID;

    @Test
    @DisplayName("Cookie를 적절히 파싱하여 Key를 기준으로 저장한다.")
    void makeCookie() {
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(new Session(JSESSIONID));

        HttpCookie httpCookie = new HttpCookie(JSESSIONID_COOKIE);

        assertEquals(httpCookie.getSession().getId(), JSESSIONID);
    }

    @Test
    @DisplayName("저장되어있는 쿠키를 헤더 형식에 맞춰서 가져올 수 있다.")
    void combineCookie() {
        HttpCookie httpCookie = new HttpCookie(JSESSIONID_COOKIE);

        assertEquals(httpCookie.combineCookie().trim(), JSESSIONID_COOKIE);
    }
}
