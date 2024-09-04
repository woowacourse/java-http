package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.techcourse.exception.UncheckedServletException;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @Test
    void fromTest() {
        HttpMethod actual = HttpMethod.from("GET");

        assertThat(actual).isEqualTo(HttpMethod.GET);
    }

    @Test
    void fromTest_whenInputNotSupportedMethod_throwException() {
        assertThatThrownBy(() -> HttpMethod.from("NONE"))
                .isExactlyInstanceOf(UncheckedServletException.class);
    }
}
