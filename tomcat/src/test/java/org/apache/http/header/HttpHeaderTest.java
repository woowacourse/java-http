package org.apache.http.header;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeaderTest {

    @Test
    @DisplayName("HttpHeader 객체 생성 성공")
    void from() {
        HttpHeader header = HttpHeader.from("Content-Type: application/json");

        assertAll(
                () -> assertEquals(HttpHeaderName.CONTENT_TYPE, header.getKey()),
                () -> assertEquals("application/json", header.getValue())
        );
    }

    @Test
    void testToString() {
        HttpHeader header = new HttpHeader(HttpHeaderName.CONTENT_TYPE, "application/json");
        assertEquals("Content-Type: application/json", header.toString());
    }
}
