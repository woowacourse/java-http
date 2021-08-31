package nextstep.jwp.http;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProtocolTest {

    @DisplayName("Protocol 객체를 생성한다.")
    @Test
    void create() {
        String value = "HTTP/1.1";
        Protocol protocol = new Protocol(value);

        assertThat(protocol.getValue()).isEqualTo(value);
    }

    @DisplayName("프로토콜 값이 비어있으면 예외를 반환한다.")
    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void createFail(String input) {
        assertThatThrownBy(() -> new Protocol(input))
            .isInstanceOf(IllegalArgumentException.class);
    }
}