package org.apache.coyote.http11.message.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpMethodTest {

    @DisplayName("이름이 일치하는 Http Method를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"GET", "POST"})
    void fromStringWithSuccess(String method) {
        HttpMethod actual = HttpMethod.from(method);

        assertThat(actual.name()).isEqualTo(method);
    }

    @DisplayName("이름이 일치하는 Http Method가 없으면 예외가 발생한다.")
    @Test
    void fromStringWithFailure() {
        String wrongMethod = "wrongMethod";

        assertThatThrownBy(() -> HttpMethod.from(wrongMethod))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
