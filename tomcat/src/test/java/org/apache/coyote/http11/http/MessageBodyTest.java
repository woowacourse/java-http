package org.apache.coyote.http11.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.IOException;
import org.apache.coyote.http11.http.domain.Headers;
import org.apache.coyote.http11.http.domain.MessageBody;
import org.junit.jupiter.api.Test;
import support.BufferedReaderFactory;

class MessageBodyTest {

    @Test
    void length() throws IOException {
        BufferedReader bufferedReader = BufferedReaderFactory.getBufferedReader("String");
        MessageBody messageBody = MessageBody.from(
                bufferedReader,
                Headers.builder()
                        .contentLength(8));

        assertThat(messageBody.length()).isEqualTo("String\r\n".getBytes().length);
    }
}
