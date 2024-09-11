package org.apache.http.header;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class HttpHeaderNameTest {

    @Nested
    @DisplayName("문자열로부터 상수 추출")
    class from {

        @Test
        @DisplayName("문자열로부터 상수 추출 성공")
        void from() {
            assertAll(
                    () -> assertEquals(HttpHeaderName.COOKIE, HttpHeaderName.from("cookie")),
                    () -> assertEquals(HttpHeaderName.CONTENT_TYPE, HttpHeaderName.from("CONTENT-TYPE"))
            );
        }

        @Test
        @DisplayName("문자열로부터 상수 추출 실패: 존재하지 않는 상수")
        void from_WhenNotExistsName() {
            assertThatThrownBy(() -> HttpHeaderName.from("INVALID"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("해당하는 HttpHeaderName이 없습니다.");
        }
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
