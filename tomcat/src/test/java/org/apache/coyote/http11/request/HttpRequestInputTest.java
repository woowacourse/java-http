package org.apache.coyote.http11.request;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

class HttpRequestInputTest {

    @Test
    void readLine_success() throws IOException {
        // given
        final HttpRequestInput input = createInput("GET /login HTTP/1.1\r\n");

        // when
        final String line = input.readLine();

        // then
        assertThat(line).isEqualTo("GET /login HTTP/1.1");
    }

    @Test
    void readLine_success_emptyLine() throws IOException {
        // given
        final HttpRequestInput input = createInput("\r\n");

        // when
        final String line = input.readLine();

        // then
        assertThat(line).isEmpty();
    }

    @Test
    void readBytes_success_utf8() throws IOException {
        // given
        final String body = "안녕";
        final byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);

        final HttpRequestInput input =
                new HttpRequestInput(
                        new ByteArrayInputStream(bodyBytes)
                );

        // when
        final byte[] result = input.readBytes(bodyBytes.length);

        // then
        assertThat(new String(result, StandardCharsets.UTF_8))
                .isEqualTo("안녕");
    }

    @Test
    void readBytes_throwsException_whenDataShorterThanLength() {
        // given
        final HttpRequestInput input = createInput("abc");

        // when & then
        assertThatThrownBy(() -> input.readBytes(5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    private HttpRequestInput createInput(
            final String value
    ) {
        return new HttpRequestInput(
                new ByteArrayInputStream(
                        value.getBytes(StandardCharsets.UTF_8)
                )
        );
    }
}
