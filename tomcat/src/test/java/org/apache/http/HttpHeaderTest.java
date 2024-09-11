package org.apache.http;

import org.apache.http.header.HttpHeader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HttpHeaderTest {

    @Test
    @DisplayName("HttpHeader 객체 생성 성공")
    void from() {
        HttpHeader header = HttpHeader.from("Content-Type: application/json");

        assertAll(
                () -> assertEquals("Content-Type", header.getKey()),
                () -> assertEquals("application/json", header.getValue())
        );
    }

    @Test
    void testToString() {
        HttpHeader header = new HttpHeader("Content-Type", "application/json");
        assertEquals("Content-Type: application/json", header.toString());
    }
}
