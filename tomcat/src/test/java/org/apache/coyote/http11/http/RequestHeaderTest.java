package org.apache.coyote.http11.http;

import org.apache.coyote.http11.handler.RequestParser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;

class RequestHeaderTest {

    @Test
    void header에_contectLength가_없으면_0_반환() throws IOException {
        final String httpRequest = String.join("\r\n",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                "");

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        RequestHeader header = RequestHeader.from(input);

        Assertions.assertThat(header.getContentLength()).isEqualTo(0);
    }

    @Test
    void header에_contectLength가_있으면_해당_값_반환() throws IOException {
        int expected = 127;

        final String httpRequest = String.join("\r\n",
                "Content-Length: " + expected + " ",
                "Connection: keep-alive ",
                "",
                "");

        BufferedReader input = RequestParser.requestToInput(httpRequest);
        RequestHeader header = RequestHeader.from(input);

        Assertions.assertThat(header.getContentLength()).isEqualTo(expected);
    }

}
