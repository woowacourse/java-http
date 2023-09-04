package org.apache.coyote.http11.response;

public class HttpResponse {
    private final HttpResponseStatusLine statusLine;
    private final HttpResponseHeaders header;
    private final HttpResponseBody responseBody;

    public HttpResponse(final HttpResponseStatusLine statusLine, final HttpResponseHeaders header,
                        final HttpResponseBody responseBody) {
        this.statusLine = statusLine;
        this.header = header;
        this.responseBody = responseBody;
    }

    @Override
    public String toString() {
        return String.join("\r\n",
                statusLine.toString(),
                header.toString(),
                "",
                responseBody.getBody());
    }
}
