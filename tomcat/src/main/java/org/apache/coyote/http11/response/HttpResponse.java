package org.apache.coyote.http11.response;

public class HttpResponse {
    private final HttpResponseStatusLine statusLine;
    private final HttpResponseHeader header;
    private final HttpResponseBody responseBody;

    public HttpResponse(final HttpResponseStatusLine statusLine, final HttpResponseHeader header,
                        final HttpResponseBody responseBody) {
        this.statusLine = statusLine;
        this.header = header;
        this.responseBody = responseBody;
    }

    public HttpResponseStatusLine getStatusLine() {
        return statusLine;
    }

    public HttpResponseHeader getHeader() {
        return header;
    }

    public HttpResponseBody getResponseBody() {
        return responseBody;
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                statusLine.toString(),
                header.toString(),
                "",
                responseBody.toString());
    }
}
