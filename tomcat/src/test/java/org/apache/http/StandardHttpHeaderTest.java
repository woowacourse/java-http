package org.apache.http;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StandardHttpHeaderTest {

    @Test
    void equalsIgnoreCase() {
        assertAll(
                () -> assertTrue(StandardHttpHeader.COOKIE.equalsIgnoreCase("cookie")),
                () -> assertTrue(StandardHttpHeader.COOKIE.equalsIgnoreCase("Cookie")),
                () -> assertTrue(StandardHttpHeader.COOKIE.equalsIgnoreCase("COOKIE"))
        );
    }
}
