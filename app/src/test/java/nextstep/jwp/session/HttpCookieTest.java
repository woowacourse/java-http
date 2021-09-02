package nextstep.jwp.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpCookieTest {
    @Test
    @DisplayName("쿠키 생성 테스트")
    void createCookie() {
        HttpCookie cookie = new HttpCookie("yummy_cookie=choco; tasty_cookie=strawberry; JSESSIONID=656cef62-e3c4-40bc-a8df-94732920ed46");
        assertEquals("choco", cookie.get("yummy_cookie"));
        assertEquals("strawberry", cookie.get("tasty_cookie"));
        assertEquals("656cef62-e3c4-40bc-a8df-94732920ed46", cookie.get("JSESSIONID"));
        assertTrue(cookie.hasSessionId());
    }
}