package org.apache.coyote.http11.domain.protocolVersion;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProtocolVersionTest {

    @Test
    @DisplayName("문자열을 Protocol과 Version을 나누어 저장한다.")
    void makeProtocolVersion() {
        String inputProtocolVersion = "HTTP/1.1";
        ProtocolVersion protocolVersion = new ProtocolVersion(inputProtocolVersion);

        assertAll(
                () -> assertEquals(protocolVersion.getVersionValue(), "1.1"),
                () -> assertEquals(protocolVersion.getProtocol(), Protocol.HTTP)
        );
    }

}
