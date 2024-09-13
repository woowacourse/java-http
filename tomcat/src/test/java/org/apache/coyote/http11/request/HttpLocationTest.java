package org.apache.coyote.http11.request;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class HttpLocationTest {

    @ParameterizedTest
    @ValueSource(strings = {"/@@#", "/..", "/?", "", "+", "[", "]", "$.;*"})
    @DisplayName("유효하지 않은 문자는 위치가 될 수 없다")
    void from(String data) {
        Assertions.assertThatThrownBy(() -> HttpLocation.from(data)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("파일 형식이 따로 지정되지 않으면 html을 반환한다.")
    void getExtension() {
        Assertions.assertThat(HttpLocation.from("/hello").getExtension()).isEqualTo("html");
    }
}
