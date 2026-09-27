package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void createsHttpResponse() {
        HttpResponse httpResponse = HttpResponse.createOkResponse(
                "text/plain",
                "Hello",
                Map.of()
        );

        assertThat(httpResponse.toString()).isEqualTo(
                "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/plain\r\n" +
                        "Content-Length: 5\r\n" +
                        "\r\n" +
                        "Hello"
        );
    }

    @Test
    void createsNotFoundResponse() {
        HttpResponse httpResponse = HttpResponse.createNotFoundResponse("Not Found");

        assertThat(httpResponse.toString()).startsWith("HTTP/1.1 404 Not Found\r\n");
    }
}
