package org.apache.coyote.http11.response;

public class HttpResponse {

    private final HttpResponseHeaders httpResponseHeaders;

    private final HttpResponseBody httpResponseBody;

    private HttpStatusCode httpStatusCode;

    public HttpResponse(HttpStatusCode httpStatusCode,
                        HttpResponseHeaders httpResponseHeaders,
                        HttpResponseBody httpResponseBody) {
        this.httpStatusCode = httpStatusCode;
        this.httpResponseHeaders = httpResponseHeaders;
        this.httpResponseBody = httpResponseBody;
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
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
