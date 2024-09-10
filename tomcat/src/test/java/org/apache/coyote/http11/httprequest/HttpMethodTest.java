package org.apache.coyote.http11.httprequest;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpMethodTest {

    @Test
    @DisplayName("일치하는 HTTP METHOD를 찾을 수 있다.")
    void from() {
        HttpMethod httpMethod = HttpMethod.from("GET");

        assertThat(httpMethod).isEqualTo(HttpMethod.GET);
    }

    @Test
    @DisplayName("일치하는 HTTP METHOD를 찾을 수 없다면 예외가 발생한다.")
    void invalidHttpMethod() {
        assertThatThrownBy(() -> HttpMethod.from("INVALID"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
