package org.apache.coyote.http11;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void headersAreWrittenInTheirAddedOrder() {
        final var response = HttpResponse.of("200 OK", "Hello".getBytes(StandardCharsets.UTF_8))
                .withHeader("Set-Cookie", "JSESSIONID=session-id")
                .withHeader("Content-Type", "text/html")
                .withHeader("Content-Length", "5");

        final var expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Set-Cookie: JSESSIONID=session-id",
                "Content-Type: text/html",
                "Content-Length: 5",
                "",
                "");

        assertThat(response.headerBytes()).isEqualTo(expected.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    void addingHeaderCreatesNewResponse() {
        final var response = HttpResponse.of("302 Found", new byte[0]);
        final var responseWithLocation = response.withHeader("Location", "/index.html");

        assertThat(new String(response.headerBytes(), StandardCharsets.UTF_8))
                .doesNotContain("Location");
        assertThat(new String(responseWithLocation.headerBytes(), StandardCharsets.UTF_8))
                .contains("Location: /index.html");
    }
}
