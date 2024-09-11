package org.apache.coyote.http;

import java.util.StringJoiner;

public class HttpResponse implements HttpComponent {

    private final HttpStatusLine statusLine;
    private final HttpHeaders headers;
    private final HttpBody body;

    HttpResponse(
            final HttpStatusLine statusLine,
            final HttpHeaders headers,
            final HttpBody body
    ) {
        this.statusLine = statusLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public HttpVersion getVersion() {
        return statusLine.getVersion();
    }

    public HttpStatusCode getStatusCode() {
        return statusLine.getStatusCode();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public HttpBody getBody() {
        return body;
    }

    public byte[] getBytes() {
        return asString().getBytes();
    }

    @Override
    public String asString() {
        final var result = new StringJoiner(LINE_FEED);
        result.add(statusLine.asString())
                .add(headers.asString())
                .add(body.asString());
        return result.toString();
    }
}
