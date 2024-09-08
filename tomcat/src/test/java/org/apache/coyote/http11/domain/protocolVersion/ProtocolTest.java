package org.apache.coyote.http11.domain.protocolVersion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ProtocolTest {

    @ParameterizedTest
    @ValueSource(strings = {"pp", "httttttp"})
    @DisplayName("일치하는 Protocol이 없는 경우 에러를 발생한다.")
    void findProtocolWhenNoCorrespondProtocol(String inputProtocol) {
        assertThrows(IllegalArgumentException.class, () -> Protocol.findProtocol(inputProtocol));
    }

    @Test
    @DisplayName("일치하는 Protocol을 찾아올 수 있다.")
    void findProtocolWhenCorrespondProtocol() {
        String inputProtocol = "HTTP";

        assertEquals(Protocol.HTTP, Protocol.findProtocol(inputProtocol));
    }
}
