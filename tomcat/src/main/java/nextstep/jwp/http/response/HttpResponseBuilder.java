package nextstep.jwp.http.response;

public class HttpResponseBuilder {

    private String version;
    private String statusCode;
    private String contentType;
    private int contentLength;
    private String responseBody;

    public HttpResponseBuilder version() {
        this.version = "HTTP/1.1";
        return this;
    }

    public HttpResponseBuilder statusCode(final String statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    public HttpResponseBuilder contentType(final String contentType) {
        this.contentType = contentType;
        return this;
    }

    public HttpResponseBuilder contentLength(final int contentLength) {
        this.contentLength = contentLength;
        return this;
    }

    public HttpResponseBuilder responseBody(final String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(version, statusCode, contentType, contentLength, responseBody);
    }
}
