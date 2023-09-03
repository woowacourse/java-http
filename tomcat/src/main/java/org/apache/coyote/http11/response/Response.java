package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.ContentType.TEXT;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.common.Status;

public class Response {

    private final Status status;
    private final ContentType contentType;
    private final Headers headers;
    private final String body;

    private Response(
            Status status,
            ContentType contentType,
            String body
    ) {
        this.status = status;
        this.contentType = contentType;
        this.headers = new Headers();
        this.body = body;
    }

    public static Response of(
            Status status,
            String contentTypeString,
            String body
    ) {
        ContentType contentType = ContentType.from(contentTypeString)
                .orElse(TEXT);
        return new Response(status, contentType, body);
    }

    public void addLocation(String location) {
        headers.addLocation(location);
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
        return "HTTP/1.1 " + status.getCode() + " " + status.name() + " \r\n"
                + "Content-Type: " + contentType.withCharset("utf-8") + " \r\n"
                + "Content-Length: " + body.getBytes().length + " \r\n"
                + headers
                + "\r\n"
                + body;
    }

}
