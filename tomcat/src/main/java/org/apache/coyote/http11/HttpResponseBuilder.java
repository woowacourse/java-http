package org.apache.coyote.http11;

public class HttpResponseBuilder {

    private final HttpHeaders headers = new HttpHeaders();

    private StatusCode statusCode;
    private String body;

    HttpResponseBuilder() {
    }

    public HttpResponseBuilder addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    public HttpResponseBuilder contentType(ContentType contentType) {
        headers.setContentType(contentType);
        return this;
    }

    public HttpResponseBuilder statusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponseBuilder body(String body) {
        this.body = body;
        return this;
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
        headers.put("Location", location);
        return statusCode(StatusCode.FOUND);
    }

    public HttpResponse build() {
        long contentLength = body == null ? 0 : body.getBytes().length;
        headers.put("Content-Length", String.valueOf(contentLength));
        return new HttpResponse(headers, statusCode, body);
    }
}
