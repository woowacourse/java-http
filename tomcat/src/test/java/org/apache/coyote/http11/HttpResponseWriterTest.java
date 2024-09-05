package org.apache.coyote.http11;

import org.apache.coyote.http11.header.Headers;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseWriterTest {
    @Test
    void some() throws IOException {

        final HttpResponse response = new HttpResponse(HttpStatusCode.OK,
                new Headers(Map.of("Content-Type", "text/html;charset=utf-8", "Content-Length", "5564")), "HTTP/1.1",
                "".getBytes());
        final OutputStream outputStream = new ByteArrayOutputStream();
        HttpResponseWriter.write(outputStream, response);
        assertThat(outputStream.toString()).contains(
                        "HTTP/1.1 200 OK \r\n",
                        "Content-Type: text/html;charset=utf-8 \r\n",
                        "Content-Length: 5564 \r\n",
                        "");
    }
}
