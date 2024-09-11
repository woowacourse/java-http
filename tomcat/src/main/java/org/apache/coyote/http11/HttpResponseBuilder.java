package org.apache.coyote.http11;

public class HttpResponseBuilder {

    private final HttpHeaders headers;
    private final StatusCode statusCode;
    private final HttpBody body;

    private HttpResponseBuilder(HttpHeaders headers, StatusCode statusCode, HttpBody body) {
        this.headers = headers;
        this.statusCode = statusCode;
        this.body = body;
    }

    HttpResponseBuilder() {
        this(new HttpHeaders(), null, HttpBody.empty());
    }

    public HttpResponseBuilder addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpResponseBuilder contentType(ContentType contentType) {
        headers.setContentType(contentType);
        return this;
    }

    public HttpResponseBuilder cookie(HttpCookie cookie) {
        headers.addCookie(cookie);
        return this;
    }

    public HttpResponseBuilder statusCode(StatusCode statusCode) {
        return new HttpResponseBuilder(headers, statusCode, body);
    }

    public HttpResponseBuilder body(HttpBody body) {
        return new HttpResponseBuilder(headers, statusCode, body);
    }

    public HttpResponseBuilder body(String content) {
        return body(new HttpBody(content));
    }

    public HttpResponseBuilder ok() {
        return statusCode(StatusCode.OK);
    }

    public HttpResponseBuilder notFound() {
        return statusCode(StatusCode.NOT_FOUND);
    }

    public HttpResponseBuilder created() {
        return statusCode(StatusCode.CREATED);
    }

    public HttpResponseBuilder found(String location) {
        headers.setLocation(location);
        return statusCode(StatusCode.FOUND);
    }

    public HttpResponse build() {
        if (statusCode == null) {
            throw new IllegalStateException("Status code is not set");
        }
        long contentLength = body.getContentLength();
        headers.setContentLength(contentLength);
        return new HttpResponse(headers, statusCode, body);
    }
}
