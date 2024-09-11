package org.apache.coyote.http11.response;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpResponseWriterTest {

    @Test
    void writeTest() throws IOException {
        HttpStatus status = HttpStatus.OK;
        HttpResponseHeaders headers = new HttpResponseHeaders(Map.of(
                "Content-Type", "text/html",
                "Content-Length", "21"
        ));
        String body = "Hello World!!";

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpResponse response = new HttpResponse(status, headers, body);
        List<String> expected = List.of(
                "HTTP/1.1 200 OK",
                "Content-Type: text/html",
                "Content-Length: 21",
                "",
                "Hello World!!");

        HttpResponseWriter.write(outputStream, response);

        String actual = outputStream.toString(StandardCharsets.UTF_8);
        assertThat(actual).contains(expected);
    }
}
