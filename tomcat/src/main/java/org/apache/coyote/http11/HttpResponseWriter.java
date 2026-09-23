package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

final class HttpResponseWriter {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CRLF = "\r\n";

    void write(
            final HttpResponse response,
            final OutputStream outputStream
    ) throws IOException {
        final var content = response.content();
        final var headerLines = new ArrayList<String>();
        headerLines.add(statusLine(response.status()));
        for (final var header : response.headers()) {
            headerLines.add(header.name() + ": " + header.value() + " ");
        }
        headerLines.add("Content-Type: " + content.contentType() + " ");
        headerLines.add("Content-Length: " + content.body().length + " ");
        headerLines.add("");
        headerLines.add("");

        outputStream.write(String.join(CRLF, headerLines).getBytes(StandardCharsets.UTF_8));
        outputStream.write(content.body());
        outputStream.flush();
    }

    private String statusLine(final HttpStatus status) {
        return HTTP_VERSION + " " + status.code() + " " + status.reasonPhrase() + " ";
    }
}
