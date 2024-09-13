package org.apache.coyote.http11.message.response;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.apache.coyote.http11.message.HttpHeaderName;
import org.apache.coyote.http11.message.HttpHeaders;
import org.junit.jupiter.api.Test;

class HttpResponseWriterTest {

    @Test
    void writeTest() throws IOException {
        // given
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        HttpHeaders headers = new HttpHeaders();
        headers.setHeader(HttpHeaderName.CONTENT_TYPE, "text/plain");
        headers.setHeader(HttpHeaderName.CONTENT_LENGTH, "13");

        HttpResponse response = new HttpResponse(HttpStatus.OK, headers, "Hello, World!".getBytes());

        // when
        HttpResponseWriter writer = new HttpResponseWriter(outputStream);
        writer.write(response);

        // then
        String expected = String.join("\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: text/plain",
                "Content-Length: 13",
                "",
                "Hello, World!");
    }
}
