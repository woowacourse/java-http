package org.apache.coyote.http11;

public class HttpResponseBuilder {

    private HttpHeader header = new HttpHeader();
    private StatusCode statusCode;
    private String body;

    HttpResponseBuilder() {
    }

    public HttpResponseBuilder addHeader(String name, String value) {
        header.put(name, value);
        return this;
    }

    public HttpResponseBuilder contentType(ContentType contentType) {
        header.setContentType(contentType);
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

    public HttpResponse build() {
        header.put("Content-Length", String.valueOf(body.getBytes().length));
        return new HttpResponse(header, statusCode, body);
    }
}
