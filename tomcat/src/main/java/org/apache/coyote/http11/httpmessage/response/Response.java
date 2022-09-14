package org.apache.coyote.http11.httpmessage.response;

import org.apache.coyote.http11.httpmessage.common.Headers;
import org.apache.coyote.http11.httpmessage.common.HttpVersion;
import org.apache.coyote.http11.httpmessage.response.statusline.HttpStatus;
import org.apache.coyote.http11.httpmessage.response.statusline.StatusLine;

public class Response {

    private StatusLine statusLine;
    private Headers headers;
    private String body;

    public Response() {
    }

    public Response ok(final String body) {
        this.statusLine = new StatusLine(HttpVersion.HTTP1_1, HttpStatus.OK);
        this.body = body;
        return this;
    }

    public Response redirect(final String location) {
        this.statusLine = new StatusLine(HttpVersion.HTTP1_1, HttpStatus.REDIRECT);
        this.headers = new Headers().add("Location", location);
        return this;
    }

    public Response addHeader(final String headerName, final String value) {
        if (headers == null) {
            headers = new Headers().add(headerName, value);
            return this;
        }
        this.headers = headers.add(headerName, value);
        return this;
    }

    public byte[] getBytes() {
        if (this.body == null) {
            return String.join("\r\n", statusLine.parseToString(), headers.parseToString()).getBytes();
        }
        return String.join("\r\n", statusLine.parseToString(), headers.parseToString() + "\r\n", body).getBytes();
    }
}
