package org.apache.coyote.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CookieUtilTest {

    @DisplayName("쿠키 형식의 여러 값들을 읽어 쿠키를 생성한다.")
    @Test
    void read() {
        List<String> values = List.of("JSESSIONID=abc123; userId=1; name=kaki");

        Cookie cookie = CookieUtil.read(values);

        Map<String, String> expectedCookies = Map.of(
                "JSESSIONID", "abc123",
                "userId", "1",
                "name", "kaki"
        );

        assertEquals(expectedCookies, cookie.getValues());
    }

    @DisplayName("빈 값을 받아 쿠키를 생성하면 쿠키의 값도 비어있다.")
    @Test
    void readEmptyValues() {
        List<String> values = List.of("");

        Cookie cookie = CookieUtil.read(values);

        assertTrue(cookie.getValues().isEmpty());
    }

    @DisplayName("단일 key=value 형식의 값을 읽어 쿠키를 생성한다.")
    @Test
    void readSingleValue() {
        List<String> values = List.of("name=kaki");

        Cookie cookie = CookieUtil.read(values);

        Map<String, String> expectedCookies = Map.of("name", "kaki");

        assertEquals(expectedCookies, cookie.getValues());
    }

    @DisplayName("잘못된 형식의 쿠키 값을 받으면 정해진 형식으로 쿠키를 생성한다.")
    @Test
    void readInvalidFormattedValues() {
        List<String> values = Arrays.asList("key1=value1; key2; key3=value3");

        Cookie cookie = CookieUtil.read(values);

        Map<String, String> expectedCookies = Map.of(
                "key1", "value1",
                "key3", "value3"
        );
        assertEquals(expectedCookies, cookie.getValues());
    }
}
