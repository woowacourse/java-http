package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class MessageBodyTest {

    @Test
    void length() throws IOException {
        InputStream inputStream = new ByteArrayInputStream("String".getBytes());
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        MessageBody messageBody = MessageBody.from(bufferedReader);

        assertThat(messageBody.length()).isEqualTo("String\r\n".getBytes().length);
    }
}
