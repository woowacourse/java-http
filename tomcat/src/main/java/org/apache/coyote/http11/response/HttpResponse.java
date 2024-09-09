package org.apache.coyote.http11.response;

public class HttpResponse {

    private final HttpStatusCode httpStatusCode;

    private final HttpResponseHeaders httpResponseHeaders;

    private final HttpResponseBody httpResponseBody;

    public HttpResponse(HttpStatusCode httpStatusCode,
                        HttpResponseHeaders httpResponseHeaders,
                        HttpResponseBody httpResponseBody) {
        this.httpStatusCode = httpStatusCode;
        this.httpResponseHeaders = httpResponseHeaders;
        this.httpResponseBody = httpResponseBody;
    }

    public HttpStatusCode getHttpStatusCode() {
        return httpStatusCode;
    }

    public HttpResponseHeaders getHttpResponseHeaders() {
        return httpResponseHeaders;
    }

    public HttpResponseBody getHttpResponseBody() {
        return httpResponseBody;
    }
}
