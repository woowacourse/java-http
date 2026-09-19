package org.apache.coyote.http11;

import java.net.URI;
import java.util.Objects;

final class HttpResponse {

    private final String contentType;
    private final String body;
    private final HttpStatus status;
    private final URI location;
    private final Cookie cookie;

    public HttpResponse(final HttpStatus status, final String contentType, final String body) {
        this(status, contentType, body, null, Cookie.empty());
    }

    public HttpResponse(HttpStatus status, final String contentType, final String body, final URI location) {
        this(status, contentType, body, Objects.requireNonNull(location), Cookie.empty());
    }

    private HttpResponse(final HttpStatus status,
                         final String contentType,
                         final String body,
                         final URI location,
                         final Cookie cookie) {
        this.contentType = Objects.requireNonNull(contentType);
        this.body = Objects.requireNonNull(body);
        this.status = Objects.requireNonNull(status);
        this.location = location;
        this.cookie = Objects.requireNonNull(cookie);
    }

    HttpResponse withCookie(final Cookie cookie) {
        return new HttpResponse(status, contentType, body, location, cookie);
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
        if (!cookie.isEmpty()) {
            headers = String.join("\r\n",
                    headers,
                    "Set-Cookie: " + cookie.toHeaderValue());
        }

        final String response = headers + "\r\n\r\n" + body;
        return response.getBytes();
    }
}
