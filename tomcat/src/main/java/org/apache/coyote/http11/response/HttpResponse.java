package org.apache.coyote.http11.response;

public class HttpResponse {

    private static final String DEFAULT_PROTOCOL = "HTTP/1.1";

    private final String protocol;
    private final HttpStatus status;
    private final String location;
    private final Cookie cookie;
    private final ContentType contentType;
    private final String responseBody;

    private HttpResponse(String protocol,
                         HttpStatus status,
                         String location,
                         Cookie cookie,
                         ContentType contentType,
                         String responseBody) {
        this.protocol = protocol;
        this.status = status;
        this.location = location;
        this.cookie = cookie;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponseBuilder ok() {
        return new HttpResponseBuilder()
                .protocol(DEFAULT_PROTOCOL)
                .status(HttpStatus.OK);
    }

    public static HttpResponseBuilder redirect() {
        return new HttpResponseBuilder()
                .protocol(DEFAULT_PROTOCOL)
                .status(HttpStatus.FOUND);
    }

    public static HttpResponseBuilder notFound() {
        return new HttpResponseBuilder()
                .protocol(DEFAULT_PROTOCOL)
                .status(HttpStatus.NOT_FOUND);
    }

    public static class HttpResponseBuilder {

        private String protocol;
        private HttpStatus status;
        private String location;
        private Cookie cookie;
        private ContentType contentType;
        private String responseBody;

        public HttpResponseBuilder protocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public HttpResponseBuilder status(HttpStatus status) {
            this.status = status;
            return this;
        }

        public HttpResponseBuilder addLocation(String location) {
            this.location = location;
            return this;
        }

        public HttpResponseBuilder addResponseBody(String responseBody, ContentType contentType) {
            this.responseBody = responseBody;
            this.contentType = contentType;
            return this;
        }

        public HttpResponseBuilder addCookie(Cookie cookie) {
            this.cookie = cookie;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(protocol, status, location, cookie, contentType, responseBody);
        }
    }

    public String parseToString() {
        StringBuilder sb = new StringBuilder();
        if (protocol != null) {
            sb.append(protocol).append(" ");
        }
        if (status != null) {
            sb.append(status.getCode()).append(" ").append(status.getText()).append(" ");
        }
        sb.append("\r\n");

        if (location != null) {
            sb.append("Location: ").append(location).append("\r\n");
        }
        if (cookie != null) {
            sb.append("Set-Cookie: ").append(cookie.parseToString()).append("\r\n");
        }
        if (responseBody != null) {
            sb.append("Content-Type: ").append(contentType.getType()).append(" \r\n");
            sb.append("Content-Length: ").append(responseBody.getBytes().length).append(" \r\n");
            sb.append("\r\n");
            sb.append(responseBody);
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "protocol='" + protocol + '\'' +
                ", status=" + status +
                ", location='" + location + '\'' +
                ", contentType=" + contentType +
                ", responseBody='" + responseBody + '\'' +
                ", cookie='" + cookie + '\'' +
                '}';
    }
}
