package org.was.Controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatusCode;

public class ResponseResult {

    private final HttpStatusCode statusCode;
    private final Map<String, String> headers;
    private final String path;

    private ResponseResult(HttpStatusCode statusCode, Map<String, String> headers, String path) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.path = path;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getPath() {
        return path;
    }

    public static Builder status(HttpStatusCode statusCode) {
        return new Builder(statusCode);
    }

    public static class Builder {

        private HttpStatusCode statusCode;
        private Map<String, String> headers = new HashMap<>();
        private String path;

        public Builder(HttpStatusCode statusCode) {
            this.statusCode = statusCode;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public ResponseResult build() {
            return new ResponseResult(statusCode, headers, path);
        }
    }
}
