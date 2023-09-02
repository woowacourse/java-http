package org.apache.coyote.http11.response;

import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.Status;

public class Response {

    private final Status status;
    private final ContentType contentType;
    private final String body;

    private Response(
            Status status,
            ContentType contentType,
            String body
    ) {
        this.status = status;
        this.contentType = contentType;
        this.body = body;
    }

    public static Response of(
            Status status,
            String contentTypeString,
            String responseBody
    ) {
        ContentType contentType = ContentType.from(contentTypeString)
                .orElseThrow(() -> new IllegalArgumentException("invalid contentType :" + contentTypeString));
        return new Response(status, contentType, responseBody);
    }

    public Status getStatus() {
        return status;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HTTP/1.1 " + status.getCode() + " " + status.name() + " \r\n"
                + "Content-Type: " + contentType.withCharset("utf-8") + " \r\n"
                + "Content-Length: " + body.getBytes().length + " \r\n"
                + "\r\n"
                + body;
    }
}
