package org.apache.coyote.http11;

public class HttpResponse {

    private String statusCode;
    private HttpHeaders httpHeaders;
    private String body;

    public HttpResponse(final String statusCode, final HttpHeaders httpHeaders, final String body) {
        this.statusCode = statusCode;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public byte[] getBytes() {
        final String join = String.join("\r\n",
                "HTTP/1.1 " + statusCode + " ",
                httpHeaders.encodingToString(),
                "",
                body);
        return join.getBytes();
    }

    public static class Builder {

        private String statusCode;
        private HttpHeaders httpHeaders;
        private String body;

        public Builder() {
        }

        public Builder statusCode(final String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder headers(final HttpHeaders httpHeaders) {
            this.httpHeaders = httpHeaders;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(statusCode, httpHeaders, body);
        }
    }
}
