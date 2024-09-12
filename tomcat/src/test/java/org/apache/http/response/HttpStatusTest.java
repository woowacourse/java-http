package org.apache.http.response;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpStatusTest {

    @Test
    @DisplayName("지정된 형식으로 반환 성공")
    void testToString() {
        assertAll(
                () -> assertEquals("200 OK", HttpStatus.OK.toString()),
                () -> assertEquals("404 Not Found", HttpStatus.NOT_FOUND.toString()),
                () -> assertEquals("302 Found", HttpStatus.FOUND.toString()),
                () -> assertEquals("500 Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR.toString())
        );
    }
}
