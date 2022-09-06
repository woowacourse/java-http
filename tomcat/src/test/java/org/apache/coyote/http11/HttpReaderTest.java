package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpReaderTest {

    @Test
    @DisplayName("read Http request")
    void readHttpRequest() {
        // given
        final String httpRequest = String.join("\r\n",
            "GET /index.html HTTP/1.1 ",
            "Host: localhost:8080 ",
            "Connection: keep-alive ",
            "Content-Length: 39",
            "Content-Type: application/x-www-form-urlencoded",
            "",
            "name=sojukang&email=kangsburg@gmail.com");
        final InputStream inputStream = new ByteArrayInputStream(httpRequest.getBytes());
        final HttpReader reader = new HttpReader(inputStream);

        // when
        String actual = reader.readHttpRequest();

        // then
        assertThat(actual).isEqualTo(httpRequest);
    }
}