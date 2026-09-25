package org.apache.coyote.http11;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class HttpResponse {
    private static final String CRLF = "\r\n";

    private HttpStatus status = HttpStatus.OK;
    private final Map<String, String> headers = new LinkedHashMap<>();
    private byte[] body = new byte[0];

    public void setStatus(final HttpStatus status) {
        this.status = Objects.requireNonNull(status, "status must not be null");
    }

    public void setBody(final String contentType, final byte[] body) {
        addHeader("Content-Type", contentType);
        this.body = body.clone();
    }

    public void sendRedirect(final String location) {
        setStatus(HttpStatus.FOUND);
        addHeader("Location", location);
        headers.remove("Content-Type");
        body = new byte[0];
    }

    public void addHeader(final String name, final String value) {
        headers.put(name, value);
    }

    public void writeTo(final OutputStream outputStream) throws IOException {
        final StringBuilder responseHead = new StringBuilder()
                .append(status.statusLine())
                .append(' ')
                .append(CRLF);
        headers.forEach((name, value) -> responseHead
                .append(name)
                .append(": ")
                .append(value)
                .append(' ')
                .append(CRLF));
        responseHead.append("Content-Length: ").append(body.length).append(' ').append(CRLF);
        responseHead.append(CRLF);

        outputStream.write(responseHead.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(body);
        outputStream.flush();
    }
}
