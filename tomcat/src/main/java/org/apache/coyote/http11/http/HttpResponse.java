package org.apache.coyote.http11.http;

public class HttpResponse {

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CHARSET_UTF_8 = ";charset=utf-8";
    private static final String CONTENT_LENGTH = "Content-Length";

    private final HttpStatus httpStatus;
    private final HttpHeaders httpHeaders;
    private final String body;

    public HttpResponse(HttpStatus httpStatus, HttpHeaders httpHeaders, String body) {
        this.httpStatus = httpStatus;
        this.httpHeaders = httpHeaders;
        this.body = body;
    }

    public String toMessage() {
        return String.join("\r\n",
                "HTTP/1.1 " + httpStatus.getValue() + " " + httpStatus.getReasonPhrase() + " ",
                httpHeaders.toMessage(),
                body);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HttpStatus httpStatus;
        private final HttpHeaders httpHeaders = new HttpHeaders();
        private String body;

        public Builder contentType(ContentType contentType) {
            httpHeaders.addHeader(CONTENT_TYPE, contentType.getMimeType() + CHARSET_UTF_8);
            return this;
        }

        public Builder httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder body(String body) {
            if (body == null) {
                return this;
            }
            this.body = body;
            httpHeaders.addHeader(CONTENT_LENGTH, String.valueOf(body.getBytes().length));
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.httpHeaders.addHeaders(headers);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(httpStatus, httpHeaders, body);
        }
    }
}
