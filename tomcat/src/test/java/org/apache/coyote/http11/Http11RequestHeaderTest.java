package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class Http11RequestHeaderTest {

    @DisplayName("BufferedReader를 읽어서 RequestHeader를 만든다.")
    @Test
    void from() throws IOException {
        String requestHeaderString = "GET /index.html HTTP/1.1\n" +
                "Host: localhost:8080\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 9\n" +
                "Accept: text/html\n" +
                "\n";
        BufferedReader bufferedReader = new BufferedReader(new StringReader(requestHeaderString));

        Http11RequestHeader requestHeader = Http11RequestHeader.from(bufferedReader);

        assertAll(
                () -> assertThat(requestHeader.getContentLength()).isEqualTo(9),
                () -> assertThat(requestHeader.getAcceptType().getFirst()).isEqualTo("text/html"),
                () -> assertThat(requestHeader.getCookie()).isEmpty()
        );
    }
}
