package org.apache.http;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpCookieTest {
    @Nested
    @DisplayName("HttpCookie 생성")
    class from {

        @Test
        @DisplayName("HttpCookie 생성 성공")
        void from() {
            String cookieString = "name=value; session=";
            HttpCookie httpCookie = HttpCookie.from(cookieString);

            assertAll(
                    () -> assertEquals("value", httpCookie.getValue("name")),
                    () -> assertEquals("", httpCookie.getValue("session"))
            );
        }

        @Test
        @DisplayName("HttpCookie 생성 성공: 형식이 잘못된 경우 null 반환")
        void from_WhenValue() {
            String cookieString = "name=value; session=; flag";
            HttpCookie httpCookie = HttpCookie.from(cookieString);

            assertAll(
                    () -> assertEquals("value", httpCookie.getValue("name")),
                    () -> assertEquals("", httpCookie.getValue("session")),
                    () -> assertNull(httpCookie.getValue("flag"))
            );
        }
    }

    @Nested
    @DisplayName("문자열에 해당하는 쿠키 값 조회")
    class getValue {

        @Test
        @DisplayName("문자열에 해당하는 쿠키 값 조회")
        void getValue() {
            HttpCookie httpCookie = HttpCookie.from("name=value");
            assertEquals("value", httpCookie.getValue("name"));
        }

        @Test
        @DisplayName("문자열에 해당하는 쿠키 값 조회: 존재하지 않는 키일 경우 null 반환")
        void getValue_ForNonexistentKey() {
            HttpCookie httpCookie = HttpCookie.from("name=value");
            assertNull(httpCookie.getValue("nonexistent"));
        }
    }
}
