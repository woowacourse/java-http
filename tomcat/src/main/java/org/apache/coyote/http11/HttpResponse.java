package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpHeaders headers;
    private final StatusCode statusCode;
    private final String body;

    public HttpResponse(HttpHeaders headers, StatusCode statusCode, String body) {
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

    public String getBody() {
        return body;
    }
}
