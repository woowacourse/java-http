package nextstep.jwp.http.response;

import nextstep.jwp.http.HttpHeader;

public class HttpResponseBuilder {

    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_LENGTH = "Content-Length";

    private String version;
    private String status;
    private HttpHeader httpHeaders = new HttpHeader();
    private String responseBody;

    public HttpResponseBuilder version() {
        this.version = "HTTP/1.1";
        return this;
    }

    public HttpResponseBuilder status(final String status) {
        this.status = status;
        return this;
    }

    public HttpResponseBuilder contentType(final String contentType) {
        this.httpHeaders.addHeader(HEADER_CONTENT_TYPE, contentType);
        return this;
    }

    public HttpResponseBuilder contentLength(final int contentLength) {
        this.httpHeaders.addHeader(HEADER_CONTENT_LENGTH, String.valueOf(contentLength));
        return this;
    }

    public HttpResponseBuilder responseBody(final String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    public HttpResponse build() {
        return new HttpResponse(version, status, httpHeaders, responseBody);
    }
}
