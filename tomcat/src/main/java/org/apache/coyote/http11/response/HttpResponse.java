package org.apache.coyote.http11.response;

public class HttpResponse {

    private final HttpResponseHeaders httpResponseHeaders;

    private HttpResponseBody httpResponseBody;

    private HttpStatusCode httpStatusCode;

    public HttpResponse() {
        this.httpStatusCode = HttpStatusCode.OK;
        this.httpResponseHeaders = new HttpResponseHeaders();
        this.httpResponseBody = new HttpResponseBody("");
    }

    public void setHttpStatusCode(HttpStatusCode httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public void setHttpResponseHeader(String key, String value) {
        this.httpResponseHeaders.setAttribute(key, value);
    }

    public void setHttpResponseBody(String body) {
        this.httpResponseBody = new HttpResponseBody(body);
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
