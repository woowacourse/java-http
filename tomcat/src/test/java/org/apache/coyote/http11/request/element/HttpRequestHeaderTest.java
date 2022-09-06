package org.apache.coyote.http11.request.element;

import static org.apache.coyote.Constants.CRLF;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import org.apache.coyote.http11.response.element.HttpMethod;
import org.junit.jupiter.api.Test;

class HttpRequestHeaderTest {

    @Test
    void find() {
        String request = String.join(CRLF, "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "Connection: keep-alive");
        HttpRequestHeader header = HttpRequestHeader.of(request);
        assertThat(header.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(header.getPath()).isEqualTo(Path.of("/index.html"));
        assertThat(header.getQuery()).isEqualTo(Query.ofUri("/index.html"));

        assertThat(header.find("Host")).isEqualTo("localhost:8080");
        assertThat(header.find("Connection")).isEqualTo("keep-alive");
    }
}
