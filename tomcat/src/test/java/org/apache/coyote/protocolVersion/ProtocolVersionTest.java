package org.apache.coyote.protocolVersion;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProtocolVersionTest {

    @Test
    @DisplayName("Protocol 과 Version을 조합하여 가져올 수 있다.")
    void getCombinedProtocolVersion() {
        ProtocolVersion protocolVersion = ProtocolVersion.ofHTTP1();

        assertEquals(protocolVersion.getCombinedProtocolVersion(), "HTTP/1.1");
    }

}
