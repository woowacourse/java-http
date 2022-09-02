package org.apache.coyote.http11;

public class HttpResponse {
    private final String response;

    public HttpResponse(final String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String body;
        private String version;
        private String status;
        private String contentType;
        private int contentLength;

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public Builder version(final HttpVersion version) {
            this.version = version.getValue();
            return this;
        }

        public Builder status(final String status) {
            this.status = status;
            return this;
        }

        public Builder contentType(final String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder contentLength(final int contentLength) {
            this.contentLength = contentLength;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(
                    String.join("\r\n",
                            version + " " + status + " ",
                            "Content-Type: " + contentType + " ",
                            "Content-Length: " + contentLength + " ",
                            "",
                            body));
        }
    }
}
