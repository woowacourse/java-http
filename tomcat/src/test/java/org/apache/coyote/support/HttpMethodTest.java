package org.apache.coyote.support;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.exception.HttpMethodNotSupportedException;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void of() {
        assertThat(HttpMethod.of("POST")).isEqualTo(HttpMethod.POST);
    }

    @Test
    void ofNotSupport() {
        assertThatThrownBy(() -> HttpMethod.of("AA"))
                .isInstanceOf(HttpMethodNotSupportedException.class);
    }
}
