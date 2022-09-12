package org.apache.coyote.http11.response;

public class HttpResponseBuilder {

    private HttpStatus status;
    private ResponseHeader responseHeader;
    private StringBuilder body;

    public HttpResponseBuilder(HttpStatus status, ResponseHeader responseHeader, StringBuilder body) {
        this.status = status;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public HttpResponseBuilder() {
        this(null, ResponseHeader.empty(), new StringBuilder());
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public HttpResponseBuilder status(HttpStatus status) {
        return new HttpResponseBuilder(status, responseHeader, body);
    }

    public HttpResponseBuilder setHeader(String key, String value) {
        responseHeader.put(key, value);
        return new HttpResponseBuilder(status, responseHeader, body);
    }

    public HttpResponseBuilder body(String body) {
        responseHeader.put("Content-Length", "" + body.getBytes().length);
        return new HttpResponseBuilder(status, responseHeader, new StringBuilder(body));
    }

    public HttpResponse redirect(String url) {
        responseHeader.put("Location", url);
        return new HttpResponse(HttpStatus.FOUND, responseHeader, new StringBuilder());
    }

    public HttpResponse methodNotAllowed() {
        return new HttpResponse(HttpStatus.METHOD_NOT_ALLOWED, ResponseHeader.empty(), new StringBuilder());
    }

    public HttpResponse build() {
        return new HttpResponse(status, responseHeader, body);
    }
}
