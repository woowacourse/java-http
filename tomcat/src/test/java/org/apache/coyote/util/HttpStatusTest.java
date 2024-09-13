package org.apache.coyote.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpStatusTest {

    @Test
    @DisplayName("HttpStatus 의 형태로 조합하여 가져온다.")
    void getCombinedHttpStatus() {
        assertEquals(HttpStatus.OK.getCombinedHttpStatus(), "200 OK");
    }
}
