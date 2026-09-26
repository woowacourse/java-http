package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;

public final class HttpResponseWriter {

    private HttpResponseWriter() {
    }

    public static void write(final HttpResponse response, final OutputStream outputStream) throws IOException {
        final HttpStatus status = response.status();
        final StringBuilder head = new StringBuilder()
                .append("HTTP/1.1 %d %s \r\n".formatted(status.code(), status.reasonPhrase()));

        Map<String, String> headers = response.headers().values();
        headers.forEach((name, value) -> head.append("%s: %s \r\n".formatted(name, value)));
        head.append("\r\n");

        outputStream.write(head.toString().getBytes(UTF_8));
        outputStream.write(response.body());
        outputStream.flush();
    }
}
