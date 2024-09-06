package org.apache.coyote.http11;

public class HttpResponseBuilder {

    private HttpHeaders headers = new HttpHeaders();
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

    public HttpResponse build() {
        headers.put("Content-Length", String.valueOf(body.getBytes().length));
        return new HttpResponse(headers, statusCode, body);
    }
}
