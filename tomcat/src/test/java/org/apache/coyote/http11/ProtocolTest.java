package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.coyote.component.Protocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProtocolTest {

    @Test
    @DisplayName("protocol 버전 확인")
    void from() {
        assertThat(Protocol.from("HTTP/1.1")).isEqualTo(Protocol.HTTP_1_1);
    }

    @Test
    @DisplayName("존재하지 않는 버전인 경우 예외 발상")
    void fail() {
        assertThatThrownBy(() -> Protocol.from("HTTP/4"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
