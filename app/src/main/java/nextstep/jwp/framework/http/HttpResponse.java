package nextstep.jwp.framework.http;

import java.util.Objects;

public class HttpResponse {

    private static final String EMPTY = "";

    private final StatusLine statusLine;
    private final HttpHeaders httpHeaders;
    private final int contentLength;
    private final String responseBody;

    public HttpResponse(StatusLine statusLine, HttpHeaders httpHeaders, int contentLength, String responseBody) {
        this.statusLine = statusLine;
        this.httpHeaders = httpHeaders;
        this.contentLength = contentLength;
        this.responseBody = Objects.requireNonNullElse(responseBody, EMPTY);
    }

    public static Builder ok() {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.OK);
        return new Builder(statusLine).header(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
    }

    public static Builder notFound() {
        final StatusLine statusLine = new StatusLine(HttpVersion.HTTP_1_1, HttpStatus.NOT_FOUND);
        return new Builder(statusLine).header(HttpHeader.CONTENT_TYPE, "text/html;charset=utf-8");
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public HttpHeaders getHttpHeaders() {
        return httpHeaders;
    }

    public long getContentLength() {
        return contentLength;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public static class Builder {
        private StatusLine statusLine;
        private HttpHeaders httpHeaders = new HttpHeaders();
        private int contentLength;
        private String responseBody;

        public Builder() {}

        public Builder(StatusLine statusLine) {
            this.statusLine = statusLine;
        }

        public Builder statusLine(StatusLine statusLine) {
            this.statusLine = statusLine;
            return this;
        }

        public Builder httpHeaders(HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder header(String headerName, String... values) {
            this.httpHeaders.addHeader(headerName, values);
            return this;
        }

        public Builder contentLength(int contentLength) {
            this.httpHeaders.addHeader(HttpHeader.CONTENT_LENGTH, Integer.toString(contentLength));
            this.contentLength = contentLength;
            return this;
        }

        public Builder body(String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusLine, httpHeaders, contentLength, responseBody);
        }
    }
}
