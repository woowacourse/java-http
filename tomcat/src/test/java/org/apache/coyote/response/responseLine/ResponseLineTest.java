package org.apache.coyote.response.responseLine;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.protocolVersion.ProtocolVersion;
import org.apache.coyote.util.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ResponseLineTest {

    private static final String HTTP_OK_RESPONSE = "HTTP/1.1 200 OK";

    @Test
    @DisplayName("Response Line의 형식으로 조합하여 가져온다.")
    void toCombinedResponse() {
        ProtocolVersion protocolVersion = ProtocolVersion.ofHTTP1();
        ResponseLine responseLine = new ResponseLine(protocolVersion);
        responseLine.setHttpStatus(HttpStatus.OK);

        assertEquals(responseLine.toCombinedResponse(), HTTP_OK_RESPONSE);
    }
}
