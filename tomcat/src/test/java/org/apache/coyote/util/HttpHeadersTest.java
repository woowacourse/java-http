package org.apache.coyote.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    private static final String locationHeader = "Location";

    @Test
    @DisplayName("헤더를 찾는다.")
    void findHeaders() {
        HttpHeaders httpHeader = HttpHeaders.findHeader(locationHeader);

        assertEquals(httpHeader, HttpHeaders.LOCATION);
    }
}
