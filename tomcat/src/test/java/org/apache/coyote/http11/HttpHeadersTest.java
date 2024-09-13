package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.coyote.component.HttpHeaders;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class HttpHeadersTest {

    @Test
    @DisplayName("헤더를 파싱한다.")
    void parse() throws IOException {
        //given
        final StringReader stringReader = new StringReader("Host: localhost:8080 \r\nCookie: myCookie ");
        final BufferedReader bufferedReader = new BufferedReader(stringReader);
        final HttpHeaders httpHeaders = HttpHeaders.parse(bufferedReader);
        //when && then
        assertThat(httpHeaders.get("Host")).isEqualTo("localhost:8080");
        assertThat(httpHeaders.get("Cookie")).isEqualTo("myCookie");
    }
}
