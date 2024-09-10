package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpHeaders headers;
    private final StatusCode statusCode;
    private final HttpBody body;

    public HttpResponse(HttpHeaders headers, StatusCode statusCode, HttpBody body) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getContent() {
        return body.getContent();
    }
}
