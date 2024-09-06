package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpMethodTest {

    @DisplayName("httpMethod name과 일치하는 HttpMethod를 반환한다.")
    @Test
    void successFindTest() {
        assertAll(
                () -> assertThat(HttpMethod.find("GET")).isEqualTo(HttpMethod.GET),
                () -> assertThat(HttpMethod.find("POST")).isEqualTo(HttpMethod.POST)
        );
    }

    @DisplayName("httpMethod name과 일치하는 HttpMethod가 없으면 예외를 반환한다.")
    @Test
    void failureFindTest() {
        assertThatThrownBy(() -> HttpMethod.find("JAZZ"))
                .isInstanceOf(IllegalArgumentException.class);
    }

}
