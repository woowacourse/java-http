package nextstep.jwp.http.http_response;

import nextstep.jwp.exception.NotValidateHttpResponse;
import nextstep.jwp.http.common.Headers;

public class JwpHttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private final StatusCode statusCode;
    private final Headers headers;
    private final String body;

    private JwpHttpResponse(StatusCode statusCode, Headers headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public static JwpHttpResponse ok(String resourceUri, String resourceFile) {
        return new Builder()
                .statusCode(StatusCode.OK)
                .header("Content-Type", JwpContentType.find(resourceUri))
                .header("Content-Length", resourceFile.getBytes().length)
                .body(resourceFile)
                .build();
    }

    public static JwpHttpResponse found(String redirectUri) {
        return new Builder()
                .statusCode(StatusCode.FOUND)
                .header("Location", "http://localhost:8080/" + redirectUri)
                .build();
    }

    public static JwpHttpResponse notFound(String resourceFile) {
        return new Builder()
                .statusCode(StatusCode.OK)
                .header("Content-Type", JwpContentType.HTML.getResourceType())
                .header("Content-Length", resourceFile.getBytes().length)
                .body(resourceFile)
                .build();
    }

    public byte[] toBytes() {
        return String.join("\r\n",
                HTTP_VERSION + " " + statusCode.toString() + " ",
                headers.toString(),
                body).getBytes();
    }

    static class Builder {
        private static final String EMPTY_RESPONSE_BODY = "";

        private StatusCode statusCode;
        private Headers headers;
        private String body;

        public Builder() {
            this.headers = new Headers();
            this.body = EMPTY_RESPONSE_BODY;
        }

        public Builder statusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder header(String key, int value) {
            return header(key, String.valueOf(value));
        }

        public Builder header(String key, String value) {
            this.headers.addHeader(key, value);
            return this;
        }

        public Builder body(String body) {
            this.body = body;
            return this;
        }

        public JwpHttpResponse build() {
            if (statusCode == null) {
                throw new NotValidateHttpResponse();
            }
            return new JwpHttpResponse(this.statusCode, this.headers, this.body);
        }
    }
}
