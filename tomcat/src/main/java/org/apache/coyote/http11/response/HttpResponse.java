package org.apache.coyote.http11.response;

public class HttpResponse {

    private final HttpResponseStatusLine httpResponseStatusLine;
    private final HttpResponseHeaders httpResponseHeaders;


    public HttpResponse(
            final HttpResponseStatusLine httpResponseStatusLine,
            final HttpResponseHeaders httpResponseHeaders
    ) {
        this.httpResponseStatusLine = httpResponseStatusLine;
        this.httpResponseHeaders = httpResponseHeaders;
    }
}
