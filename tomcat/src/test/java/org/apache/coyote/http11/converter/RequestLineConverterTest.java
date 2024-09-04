package org.apache.coyote.http11.converter;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.coyote.http11.domain.RequestLine;
import org.apache.coyote.http11.domain.RequestMethod;
import org.apache.coyote.http11.domain.protocolVersion.Protocol;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RequestLineConverterTest {

    @Test
    @DisplayName("요청의 첫 줄을 RequestLine으로 만든다.")
    void convertFrom() {
        String inputRequestLine = "GET /index.html HTTP/1.1";

        RequestLine requestLine = RequestLineConverter.convertFrom(inputRequestLine);

        assertAll(
                () -> assertEquals(requestLine.getRequestPathValue(), "/index.html"),
                () -> assertEquals(requestLine.getRequestMethod(), RequestMethod.GET),
                () -> assertEquals(requestLine.getProtocolVersion().getProtocol(), Protocol.HTTP),
                () -> assertEquals(requestLine.getProtocolVersion().getVersionValue(), "1.1")
        );
    }
}
