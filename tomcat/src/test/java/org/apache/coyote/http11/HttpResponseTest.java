package org.apache.coyote.http11;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

class HttpResponseTest {

    @Test
    void writesUtf8BodyWithByteLength() throws IOException {
        HttpResponse response = new HttpResponse(
                new StatusLine("HTTP/1.1", 200, "OK"),
                Map.of("Content-Type", "text/plain;charset=utf-8"),
                "안녕".getBytes(StandardCharsets.UTF_8)
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        response.writeTo(output);

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/plain;charset=utf-8\r\n"
                        + "Content-Length: 6\r\n\r\n"
                        + "안녕"
        );
    }

    @Test
    void preservesBinaryBody() throws IOException {
        byte[] body = {0, (byte) 0xff, (byte) 0x80};
        HttpResponse response = new HttpResponse(
                new StatusLine("HTTP/1.1", 200, "OK"),
                Map.of(),
                body
        );
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ByteArrayOutputStream expected = new ByteArrayOutputStream();
        expected.write("HTTP/1.1 200 OK\r\nContent-Length: 3\r\n\r\n".getBytes(StandardCharsets.UTF_8));
        expected.write(body);
        response.writeTo(output);

        assertThat(output.toByteArray()).isEqualTo(expected.toByteArray());
    }
}
