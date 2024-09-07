package org.apache.coyote.http11.component;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class StatusCodeTest {

    @Test
    @DisplayName("상태코드 정보를 가지는 객체를 생성한다.")
    void generate_class_that_has_status_code_info() {
        // given
        final var name = "OK";
        final var value = 200;

        // when
        final var statusCode = new StatusCode(name, value);

        // then
        assertThat(statusCode.getResponseText()).isEqualTo("200 OK");
    }

}
