package org.apache.coyote.http11;

import java.io.IOException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RequestLineTest {
    @Test
    void separatesPathAndDecodedQueryParameters() throws IOException {
        final var line = RequestLine.from("GET /login?name=young+gi HTTP/1.1");

        assertThat(line.method()).isEqualTo("GET");
        assertThat(line.uri().path()).isEqualTo("/login");
        assertThat(line.uri().queryParameter("name")).isEqualTo("young gi");
        assertThat(line.version()).isEqualTo("HTTP/1.1");
    }

    @Test
    void rejectsMissingRequestLine() {
        assertThatThrownBy(() -> RequestLine.from(null))
                .isInstanceOf(IOException.class)
                .hasMessage("Request line is required");
    }
}
