package org.apache.coyote.http11.http;

import java.util.StringJoiner;

public class HttpResponse {

    private final String httpVersion;
    private final HttpStatusCode httpStatusCode;
    private final Headers headers;
    private final String body;

    public HttpResponse(String httpVersion, HttpStatusCode httpStatusCode, Headers headers, String body) {
        this.httpVersion = httpVersion;
        this.httpStatusCode = httpStatusCode;
        this.headers = headers;
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        final var result = new StringJoiner("\r\n");
        final var statusLine = httpVersion + " " + httpStatusCode + " ";
        result.add(statusLine)
                .add(headers.toString())
                .add(body);
        return result.toString();
    }
}
