package org.apache.coyote.http11;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpVersionTest {

    @Test
    void throwExceptionWhenCreateFromInvalidVersion() {
        //given
        final var invalidVersion = "HTTP/4.0";

        //expect
        assertThatThrownBy(() -> HttpVersion.from(invalidVersion))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown HTTP version: " + invalidVersion);
    }

}
