package org.apache.http.header;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderNameTest {

    @Test
    @DisplayName("문자열로부터 상수 파싱")
    void from() {
        assertAll(
                () -> assertEquals(HttpHeaderName.COOKIE, HttpHeaderName.from("cookie")),
                () -> assertEquals(HttpHeaderName.CONTENT_TYPE, HttpHeaderName.from("CONTENT-TYPE"))
        );
    }

    @Test
    @DisplayName("대소문자를 구분하지 않고 비교")
    void equalsIgnoreCase() {
        assertAll(
                () -> assertTrue(HttpHeaderName.COOKIE.equalsIgnoreCase("cookie")),
                () -> assertTrue(HttpHeaderName.COOKIE.equalsIgnoreCase("Cookie")),
                () -> assertTrue(HttpHeaderName.COOKIE.equalsIgnoreCase("COOKIE"))
        );
    }
}
