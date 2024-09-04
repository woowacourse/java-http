package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class HttpRequestCreatorTest {

    @Test
    void createStartLineTest() throws IOException {
        String startLine = "GET / HTTP/1.1";
        BufferedReader reader = new BufferedReader(new StringReader(startLine));
        RequestStartLine expected = new RequestStartLine("GET", "/", "HTTP/1.1");

        RequestStartLine actual = HttpRequestCreator.createStartLine(reader);

        assertThat(actual).isEqualTo(expected);
    }
}
