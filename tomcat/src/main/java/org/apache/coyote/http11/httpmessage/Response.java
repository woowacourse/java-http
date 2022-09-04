package org.apache.coyote.http11.httpmessage;

import org.apache.coyote.http11.httpmessage.statusline.HttpStatus;
import org.apache.coyote.http11.httpmessage.statusline.StatusLine;

public class Response {

    private StatusLine statusLine;
    private Headers headers;
    private String body;

    public Response(StatusLine statusLine, Headers headers, String body) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public Response(StatusLine statusLine, Headers headers) {
        this.statusLine = statusLine;
        this.headers = headers;
    }

    public Response() {
    }

    public static Response okWithResponseBody(final ContentType contentType, final String responseBody) {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP1_1, HttpStatus.OK);

        final Headers headers = new Headers()
                .add("Content-Type", contentType.getContentType() + ";charset=utf-8")
                .add("Content-Length", String.valueOf(responseBody.getBytes().length));

        return new Response(statusLine, headers, responseBody);
    }

    public static Response redirect(final ContentType contentType, final String location) {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP1_1, HttpStatus.REDIRECT);

        final Headers headers = new Headers()
                .add("Location", location)
                .add("Content-Type", contentType.getContentType() + ";charset=utf-8");

        return new Response(statusLine, headers);
    }

    public byte[] getBytes() {
        if (this.body == null) {
            return String.join("\r\n", statusLine.parseToString(), headers.parseToString()).getBytes();
        }
        return String.join("\r\n", statusLine.parseToString(), headers.parseToString() + "\r\n", body).getBytes();
    }
}
