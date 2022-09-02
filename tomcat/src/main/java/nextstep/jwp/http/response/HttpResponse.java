package nextstep.jwp.http.response;

public class HttpResponse {

    private String version;
    private String statusCode;
    private String statusMessage;
    private String contentType;
    private int contentLength;
    private String responseBody;

    public static class Builder {

        private String version;
        private String statusCode;
        private String statusMessage;
        private String contentType;
        private int contentLength;
        private String responseBody;

        public Builder version() {
            this.version = "HTTP/1.1";
            return this;
        }

        public Builder statusCode(final String statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder statusMessage(final String statusMessage) {
            this.statusMessage = statusMessage;
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

        public Builder responseBody(final String responseBody) {
            this.responseBody = responseBody;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }

    public HttpResponse(final Builder builder) {
        this.version = builder.version;
        this.statusCode = builder.statusCode;
        this.statusMessage = builder.statusMessage;
        this.contentType = builder.contentType;
        this.contentLength = builder.contentLength;
        this.responseBody = builder.responseBody;
    }


    public String getTemplate() {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + this.contentType + ";charset=utf-8 ",
                "Content-Length: " + this.responseBody.getBytes().length + " ",
                "",
                this.responseBody);
    }
}
