package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HttpResponseTest {

    @Test
    void writesBodyWithByteLength() throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(output);

        response.addHeader("Content-Type", "text/plain;charset=utf-8");
        response.setBody("안녕".getBytes(StandardCharsets.UTF_8));
        response.write();

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/plain;charset=utf-8\r\n"
                        + "Content-Length: 6\r\n"
                        + "\r\n"
                        + "안녕"
        );
    }

    @Test
    void writesRedirectWithoutBody() throws IOException {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        final HttpResponse response = new HttpResponse(output);

        response.setStatus(302, "Found");
        response.addHeader("Location", "/index.html");
        response.write();

        assertThat(output.toString(StandardCharsets.UTF_8)).isEqualTo(
                "HTTP/1.1 302 Found\r\n"
                        + "Location: /index.html\r\n"
                        + "Content-Length: 0\r\n"
                        + "\r\n"
        );
    }
}
