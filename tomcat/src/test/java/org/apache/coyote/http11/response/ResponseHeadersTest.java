package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

class ResponseHeadersTest {

    @Test
    void createResponseHeaders() {
        // given
        String http = String.join("\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        HttpRequest request = HttpRequest.parse(new ByteArrayInputStream(http.getBytes()));
        ResponseEntity entity = new ResponseEntity(HttpStatus.OK, "hello world");
        // when

        ResponseHeaders headers = ResponseHeaders.from(request, entity);
        // then
        assertThat(headers).isNotNull();
    }

    @Test
    void getAsString() {
        // given
        String http = String.join("\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        HttpRequest request = HttpRequest.parse(new ByteArrayInputStream(http.getBytes()));
        ResponseEntity entity = new ResponseEntity(HttpStatus.OK, "hello world");
        ResponseHeaders headers = ResponseHeaders.from(request, entity);
        // when
        String headersString = headers.getAsString();
        // then
        assertThat(headersString).isEqualTo("Content-Type: text/plain\nContent-Length: 11");
    }
}
