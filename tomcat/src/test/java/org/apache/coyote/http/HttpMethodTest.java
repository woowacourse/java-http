package org.apache.coyote.http;

import org.apache.coyote.http.request.HttpMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {

    @Test
    @DisplayName("")
    void validateMethod1() {
        String test = "GETT";

        assertThat(HttpMethod.isHttpMethod(test)).isFalse();
    }

    @Test
    @DisplayName("")
    void validateMethod2() {
        String test = "GETT";

        assertThatThrownBy(() -> HttpMethod.findByMethod(test))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unknown http method: GETT");
    }

}
