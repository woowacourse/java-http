package org.apache.coyote.http11.message;

import org.apache.coyote.http11.ContentType;

public class HttpResponse {

    private final String value;

    public HttpResponse(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private HttpStatus httpStatus;
        private final HttpHeaders headers = new HttpHeaders();
        private String body;

        public Builder contentType(ContentType contentType) {
            headers.addHeader("Content-Type", contentType.getMimeType() + ";charset=utf-8");
            return this;
        }

        public Builder httpStatus(HttpStatus httpStatus) {
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            headers.addHeader("Content-Length", String.valueOf(body.getBytes().length));
            return this;
        }

        public Builder headers(HttpHeaders headers) {
            this.headers.addHeaders(headers);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(
                    String.join("\r\n",
                            "HTTP/1.1 " + httpStatus.getValue() + " " + httpStatus.getReasonPhrase() + " ",
                            headers.toMessage(),
                            body)
            );
        }
    }
}
