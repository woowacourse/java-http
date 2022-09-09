package org.apache.coyote.http11.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class RequestLineTest {
    @Test
    void BufferedReader에서_첫_줄을_읽어서_RequestLine을_생성() {
        // given
        final String request = String.join(System.lineSeparator(), "GET /index.html HTTP/1.1 ",
                "Host: localhost:8080 ");
        final BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new ByteArrayInputStream(request.getBytes(StandardCharsets.UTF_8))));
        // when

        // then
//        assertThat(actual).isEqualTo(expected);
    }

}