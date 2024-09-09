package org.apache.http;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpCookieTest {

    @Test
    @DisplayName("HttpCookie 생성: 문자열로부터 파싱")
    void of() {
        String cookieString = "name=value; session=12345";
        HttpCookie httpCookie = HttpCookie.of(cookieString);

        assertEquals("value", httpCookie.getValue("name"));
        assertEquals("12345", httpCookie.getValue("session"));
    }

    @Test
    @DisplayName("특정 key에 대한 value 조회: 존재하지 않는 키일 경우 null 반환")
    void getValue_For_NonexistentKey() {
        HttpCookie httpCookie = HttpCookie.of("name=value");
        assertNull(httpCookie.getValue("nonexistent"));
    }

    @Test
    @DisplayName("특정 key에 대한 value 조회: 존재하지 않는 키일 경우 null 반환")
    void testGetValueForNonexistentKey() {
        HttpCookie httpCookie = HttpCookie.of("name=value");
        assertEquals("value", httpCookie.getValue("name"));
    }
}
