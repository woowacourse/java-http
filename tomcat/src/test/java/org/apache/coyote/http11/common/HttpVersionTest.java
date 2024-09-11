package org.apache.coyote.http11.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class HttpVersionTest {

    @Test
    @DisplayName("입력된 값에 해당하는 Http Version을 찾을 수 있다.")
    void from() {
        HttpVersion httpVersion = HttpVersion.from("HTTP/1.1");

        assertThat(httpVersion).isEqualTo(HttpVersion.HTTP_1_1);
    }

    @Test
    @DisplayName("입력된 값에 해당하는 Http Version이 없으면 예외가 발생한다.")
    void invalidHttpVersion() {
        assertThatThrownBy(() -> HttpVersion.from("invalid"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
