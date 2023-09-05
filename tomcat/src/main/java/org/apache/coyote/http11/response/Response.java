package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.ContentType.HTML;
import static org.apache.coyote.http11.common.ContentType.TEXT;
import static org.apache.coyote.http11.common.Status.FOUND;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.Status;

public class Response {

    private static final String LINE_SEPARATOR = System.lineSeparator();

    private final Status status;
    private final ContentType contentType;
    private final Headers headers;
    private final String body;

    private Response(
            final Status status,
            final ContentType contentType,
            final String body
    ) {
        this.status = status;
        this.contentType = contentType;
        this.headers = new Headers();
        this.body = body;
    }

    public static Response of(
            final Status status,
            final String contentTypeString,
            final String body
    ) {
        final var contentType = ContentType.from(contentTypeString)
                .orElse(TEXT);

        return new Response(status, contentType, body);
    }

    public static Response redirect(final String location) {
        final var response = Response.of(FOUND, HTML.toString(), "");
        response.addLocation(location);

        return response;
    }

    public void addLocation(final String location) {
        headers.addLocation(location);
    }

    public void addSetCookie(final String cookie) {
        headers.addSetCookie(cookie);
    }

    public Status getStatus() {
        return status;
    }

    public String getLocation() {
        return headers.getLocation();
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        return "HTTP/1.1 " + status.getCode() + " " + status.name() + LINE_SEPARATOR
                + "Content-Type: " + contentType.withCharset("utf-8") + LINE_SEPARATOR
                + "Content-Length: " + body.getBytes().length + LINE_SEPARATOR
                + headers
                + LINE_SEPARATOR
                + body;
    }
}
