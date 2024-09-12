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

    @Test
    @DisplayName("상태 코드 숫자와 일치하는 HttpStatus 반환")
    void getHttpStatus() {
        assertAll(
                () -> assertEquals(HttpStatus.OK, HttpStatus.getHttpStatus(200)),
                () -> assertEquals(HttpStatus.NOT_FOUND, HttpStatus.getHttpStatus(404)),
                () -> assertEquals(HttpStatus.FOUND, HttpStatus.getHttpStatus(302)),
                () -> assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, HttpStatus.getHttpStatus(500))
        );
    }
}
