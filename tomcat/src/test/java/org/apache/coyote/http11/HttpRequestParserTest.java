package org.apache.coyote.http11;

import org.apache.coyote.http11.method.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class HttpRequestParserTest {
    @Test
    void some() throws IOException {

        final byte[] requestBytes =
                String.join("\r\n",
                                "GET /index.html HTTP/1.1 ",
                                "Host: localhost:8080 ",
                                "Connection: keep-alive ",
                                "",
                                "")
                        .getBytes();

        final HttpRequest request = HttpRequestParser.parse(new ByteArrayInputStream(requestBytes));
        assertThat(request.getMethod()).isEqualTo(HttpMethod.GET);
        assertThat(request.getPath()).isEqualTo("/index.html");
        assertThat(request.getVersion()).isEqualTo("HTTP/1.1");
        assertThat(request.getHeaders().get("Connection")).isEqualTo("keep-alive ");
        assertThat(request.getHeaders().get("Host")).isEqualTo("localhost:8080 ");


    }

}
