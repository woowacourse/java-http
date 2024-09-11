package org.apache.http.header;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HttpHeaderNameTest {

    @Test
    void equalsIgnoreCase() {
        assertAll(
                () -> assertTrue(HttpHeaderName.COOKIE.equalsIgnoreCase("cookie")),
                () -> assertTrue(HttpHeaderName.COOKIE.equalsIgnoreCase("Cookie")),
                () -> assertTrue(HttpHeaderName.COOKIE.equalsIgnoreCase("COOKIE"))
        );
    }
}
