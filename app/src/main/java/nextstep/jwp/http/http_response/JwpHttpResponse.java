package nextstep.jwp.http.http_response;

import nextstep.jwp.exception.NotValidateHttpResponse;
import nextstep.jwp.http.common.Headers;
import nextstep.jwp.http.common.HttpCookie;

public class JwpHttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String NOT_FOUND_ERROR_PAGE = "404.html";
    private static final String INTERNAL_SERVER_ERROR_PAGE = "500.html";

    private final StatusCode statusCode;
    private final Headers headers;
    private final String body;

    public JwpHttpResponse(StatusCode statusCode, Headers headers, String body) {
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
                .location(redirectUri)
                .build();
    }

    public static JwpHttpResponse notFound() {
        return new Builder()
                .statusCode(StatusCode.NOT_FOUND)
                .location(NOT_FOUND_ERROR_PAGE)
                .build();
    }

    public static JwpHttpResponse internalServerError() {
        return new Builder()
                .statusCode(StatusCode.INTERNAL_SERVER_ERROR)
                .location(INTERNAL_SERVER_ERROR_PAGE)
                .build();
    }

    public byte[] toBytes() {
        return String.join("\r\n",
                HTTP_VERSION + " " + statusCode.toString() + " ",
                headers.toString(),
                body).getBytes();
    }

    public static class Builder {

        private static final String EMPTY_RESPONSE_BODY = "";
        private static final String DEFAULT_HOST = "http://localhost:8080/";

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

        public Builder location(String location) {
            this.headers.addHeader("Location", DEFAULT_HOST + location);
            return this;
        }

        public Builder cookie(String value) {
            this.headers.addHeader("Set-Cookie", "JSESSIONID=" + value);
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
