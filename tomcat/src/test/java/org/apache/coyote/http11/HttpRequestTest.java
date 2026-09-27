package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.BufferedReader;
import java.io.StringReader;
import org.junit.jupiter.api.Test;

class HttpRequestTest {

    @Test
    void readsRequestLineHeadersAndBody() throws Exception {
        String requestMessage = String.join("\r\n",
                "POST /login HTTP/1.1",
                "Host: localhost:8080",
                "Content-Length: 31",
                "",
                "account=gugu&password=password");

        HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(requestMessage)));

        assertThat(request.getRequestLine().getMethod()).isEqualTo("POST");
        assertThat(request.getRequestLine().getPath()).isEqualTo("/login");
        assertThat(request.getRequestLine().getProtocolVersion()).isEqualTo("HTTP/1.1");
        assertThat(request.getHeaders().get("Host")).isEqualTo("localhost:8080");
        assertThat(request.getBody()).isEqualTo("account=gugu&password=password");
    }

    @Test
    void usesEmptyBodyWhenContentLengthIsMissing() throws Exception {
        String requestMessage = String.join("\r\n",
                "GET /index.html HTTP/1.1",
                "Host: localhost:8080",
                "",
                "");

        HttpRequest request = HttpRequest.from(new BufferedReader(new StringReader(requestMessage)));

        assertThat(request.getBody()).isEmpty();
    }
}
