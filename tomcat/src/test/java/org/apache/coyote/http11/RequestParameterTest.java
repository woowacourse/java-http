package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestParameterTest {

    @DisplayName("requestParameter를 분리하여 저장한다.")
    @Test
    void requestParameter를_분리하여_저장한다() {
        // given
        String parameter = "account=mat";

        // when
        RequestParameter actual = new RequestParameter(parameter);

        // then
        assertAll(() -> {
            assertThat(actual.getKey()).isEqualTo("account");
            assertThat(actual.getValue()).isEqualTo("mat");
        });
    }
}
