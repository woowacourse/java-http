package org.apache.coyote.http11;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Cookie 테스트")
class HttpCookieTest {

    @DisplayName("빈 쿠키 헤더 처리 테스트")
    @Test
    void testEmptyCookieHeader() {
        HttpCookie cookie = new HttpCookie("");
        assertTrue(cookie.getCookies().isEmpty());
    }

    @DisplayName("null 쿠키 헤더 처리 테스트")
    @Test
    void testNullCookieHeader() {
        HttpCookie cookie = new HttpCookie(null);
        assertTrue(cookie.getCookies().isEmpty());
    }

    @DisplayName("단일 쿠키 처리 테스트")
    @Test
    void testSingleCookie() {
        HttpCookie cookie = new HttpCookie("username=Chocochip");
        assertEquals("Chocochip", cookie.getCookie("username"));
        assertTrue(cookie.getCookies().containsKey("username"));
        assertEquals(1, cookie.getCookies().size());
    }

    @DisplayName("다중 쿠키 처리 테스트")
    @Test
    void testMultipleCookies() {
        HttpCookie cookie = new HttpCookie("username=Chocochip; sessionToken=abc123; theme=dark");
        Map<String, String> cookies = cookie.getCookies();

        assertEquals(3, cookies.size());
        assertEquals("Chocochip", cookies.get("username"));
        assertEquals("abc123", cookies.get("sessionToken"));
        assertEquals("dark", cookies.get("theme"));
    }

    @DisplayName("값이 없는 쿠키 처리 테스트")
    @Test
    void testMalformedCookie_NoValue() {
        HttpCookie cookie = new HttpCookie("username=");
        assertTrue(cookie.getCookies().containsKey("username"));
        assertEquals("", cookie.getCookie("username"));
    }

    @DisplayName("키가 없는 쿠키 처리 테스트")
    @Test
    void testMalformedCookie_NoKey() {
        HttpCookie cookie = new HttpCookie("=Chocochip");
        assertTrue(cookie.getCookies().containsValue("Chocochip"));
    }

    @DisplayName("쿠키에 공백이 포함된 경우 처리 테스트")
    @Test
    void testWhitespaceAroundCookie() {
        HttpCookie cookie = new HttpCookie(" username = Chocochip ; sessionToken = abc123 ");
        assertEquals("Chocochip", cookie.getCookie("username"));
        assertEquals("abc123", cookie.getCookie("sessionToken"));
        assertEquals(2, cookie.getCookies().size());
    }

    @DisplayName("JSESSIONID 처리 테스트")
    @Test
    void testJSessionIdHandling() {
        HttpCookie cookie = new HttpCookie("JSESSIONID=xyz123");
        assertEquals("xyz123", cookie.getJsessionid());
        assertTrue(cookie.containsJSessionId());
    }

    @DisplayName("JSESSIONID가 없는 경우 처리 테스트")
    @Test
    void testJSessionIdHandling_NotPresent() {
        HttpCookie cookie = new HttpCookie("username=Chocochip");
        assertNull(cookie.getJsessionid());
        assertFalse(cookie.containsJSessionId());
    }

    @DisplayName("빈 쿠키 쌍 처리 테스트")
    @Test
    void testEmptyCookiePair() {
        HttpCookie cookie = new HttpCookie(";username=Chocochip;;");
        assertEquals("Chocochip", cookie.getCookie("username"));
        assertEquals(1, cookie.getCookies().size());
    }

    @DisplayName("특수 문자가 포함된 쿠키 처리 테스트")
    @Test
    void testCookieWithSpecialCharacters() {
        HttpCookie cookie = new HttpCookie("token=abc!@#123$%^;username=Chocochip");
        assertEquals("abc!@#123$%^", cookie.getCookie("token"));
        assertEquals("Chocochip", cookie.getCookie("username"));
    }

    @Test
    @DisplayName("쿠키 설정 메서드 테스트")
    void testSetCookieMethod() {
        HttpCookie cookie = new HttpCookie("username=Chocochip");
        cookie.getCookies().put("sessionToken", "abc123");
        assertEquals("abc123", cookie.getCookie("sessionToken"));
        assertEquals(2, cookie.getCookies().size());
    }
}
