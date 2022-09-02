package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayInputStream;
import org.apache.coyote.http11.ResponseEntity;
import org.apache.coyote.http11.request.HttpRequest;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void createResponseWithRequestAndEntity() {
        // given
        String http = String.join("\n",
                "GET / HTTP/1.1",
                "Host: localhost:8080 ",
                "Connection: keep-alive ",
                "",
                ""
        );
        HttpRequest request = HttpRequest.parse(new ByteArrayInputStream(http.getBytes()));
        ResponseEntity entity = new ResponseEntity(HttpStatus.OK, "hello world", determineContentType(path));
        // when
        HttpResponse response = HttpResponse.from(entity);
        // then
        assertThat(response).isNotNull();
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
        ResponseEntity entity = new ResponseEntity(HttpStatus.OK, "hello world", determineContentType(path));
        HttpResponse response = HttpResponse.from(entity);

        // when
        String responseString = response.getAsString();

        // then
        assertThat(responseString).isEqualTo(
                String.join("\n",
                        "HTTP/1.1 200 OK",
                        "Content-Type: text/plain",
                        "Content-Length: 11",
                        "",
                        "hello world")
        );
    }
}
