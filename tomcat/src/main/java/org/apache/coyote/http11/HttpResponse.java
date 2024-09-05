package org.apache.coyote.http11;

public class HttpResponse {

    private final HttpHeader header;
    private final StatusCode statusCode;
    private final String body;

    public HttpResponse(HttpHeader header, StatusCode statusCode, String body) {
        this.header = header;
        this.statusCode = statusCode;
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public HttpHeader getHeader() {
        return header;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getBody() {
        return body;
    }
}
