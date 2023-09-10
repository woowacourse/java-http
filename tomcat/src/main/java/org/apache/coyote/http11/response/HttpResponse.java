package org.apache.coyote.http11.response;

public class HttpResponse {

    private final HttpResponseStatusLine statusLine;
    private final HttpResponseHeaders header;
    private HttpResponseBody responseBody;

    public HttpResponse(final String httpVersion) {
        this(new HttpResponseStatusLine(httpVersion),
                new HttpResponseHeaders(),
                new HttpResponseBody());
    }

    public HttpResponse(final HttpResponseStatusLine statusLine, final HttpResponseHeaders header,
                        final HttpResponseBody responseBody) {
        this.statusLine = statusLine;
        this.header = header;
        this.responseBody = responseBody;
    }

    public HttpResponse setHttpVersion(final String version) {
        this.statusLine.setHttpVersion(version);
        return this;
    }

    public HttpResponse setStatusCode(final HttpStatusCode statusCode) {
        this.statusLine.setStatusCode(statusCode);
        return this;
    }

    public HttpResponse addHeader(final ResponseHeaderType headerType, final Object value) {
        this.header.add(headerType, value);
        return this;
    }

    public HttpResponse setResponseBody(final HttpResponseBody httpResponseBody) {
        this.responseBody = httpResponseBody;
        return this;
    }

    public HttpResponseStatusLine getStatusLine() {
        return statusLine;
    }

    public HttpResponseHeaders getHeader() {
        return header;
    }

    public HttpResponseBody getResponseBody() {
        return responseBody;
    }
}
