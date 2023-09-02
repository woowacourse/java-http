package org.apache.coyote.http11;

public class HttpResponse {

    private static final String CRLF = "\r\n";

    private final HttpResponseStatusLine responseLine;
    private final HttpHeaders headers;
    private final String body;

    public HttpResponse(final HttpResponseStatusLine responseLine, final HttpHeaders headers, final String body) {
        this.responseLine = responseLine;
        this.headers = headers;
        this.body = body;
    }

    public HttpResponse(final String contentType, final String body) {
        this(HttpResponseStatusLine.OK(), HttpHeaders.makeHttpResponseHeaders(contentType, body), body);
    }

    public HttpResponseStatusLine getResponseLine() {
        return responseLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public byte[] getBytes() {
        return toString().getBytes();
    }

    @Override
    public String toString() {
        return String.join(CRLF, responseLine.toString(), headers.toString(), body);
    }
}
