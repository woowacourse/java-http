package org.apache.coyote.http11;

import org.apache.coyote.http11.header.Headers;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.version.HttpVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseWriterTest {
    @Test
    @DisplayName("HTTP Response 응답을 OutputStream 에 작성한다.")
    void write_response_to_output_stream() throws IOException {

        final HttpResponse response = new HttpResponse(HttpStatusCode.OK,
                new Headers(Map.of("Content-Type", "text/html;charset=utf-8", "Content-Length", "5564")), HttpVersion.HTTP_1_1,
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
