package org.was.Controller;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpStatusCode;

public class ResponseResult {

    private final HttpStatusCode statusCode;
    private final Map<String, String> headers;
    private final String path;
    private final String body;

    private ResponseResult(HttpStatusCode statusCode, Map<String, String> headers, String path, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.path = path;
        this.body = body;
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

    public String getBody() {
        return body;
    }

    public static Builder status(HttpStatusCode statusCode) {
        return new Builder(statusCode);
    }

    public static class Builder {

        private HttpStatusCode statusCode;
        private Map<String, String> headers = new HashMap<>();
        private String path;
        private String body;

        public Builder(HttpStatusCode statusCode) {
            this.statusCode = statusCode;
        }

        public Builder header(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public ResponseResult path(String path) {
            this.path = path;
            return new ResponseResult(statusCode, headers, path, body);
        }

        public ResponseResult body(String body) {
            this.body = body;
            return new ResponseResult(statusCode, headers, path, body);
        }

        public ResponseResult build() {
            return new ResponseResult(statusCode, headers, path, body);
        }
    }
}
