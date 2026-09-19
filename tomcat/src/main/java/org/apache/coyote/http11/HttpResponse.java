package org.apache.coyote.http11;

import java.net.URI;
import java.util.Objects;

final class HttpResponse {

    private final String contentType;
    private final String body;
    private final HttpStatus status;
    private final URI location;

    public HttpResponse(final HttpStatus status, final String contentType, final String body) {
        this.contentType = Objects.requireNonNull(contentType);
        this.body = Objects.requireNonNull(body);
        this.status = Objects.requireNonNull(status);
        this.location = null;
    }

    public HttpResponse(HttpStatus status, final String contentType, final String body, final URI location) {
        this.contentType = Objects.requireNonNull(contentType);
        this.body = Objects.requireNonNull(body);
        this.status = Objects.requireNonNull(status);
        this.location = Objects.requireNonNull(location);
    }

    public byte[] toBytes() {
        String headers = String.join("\r\n",
                "HTTP/1.1 " + status.toString() + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.getBytes().length + " ");

        if (location != null) {
            headers = String.join("\r\n",
                    headers,
                    "Location: " + location.toString() + " ");
        }

        final String response = headers + "\r\n\r\n" + body;
        return response.getBytes();
    }
}
