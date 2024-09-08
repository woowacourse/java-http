package org.apache.coyote.http11.response;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

public class HttpResponse {
    private static final String HTTP_VERSION = "HTTP/1.1";

    private HttpStatus status;
    private HttpResponseHeaders headers;
    private String body;

    public HttpResponse(HttpStatus status, HttpResponseHeaders headers, String body) {
        this.status = status;
        this.headers = headers;
        this.body = body;
    }

    public static HttpResponseBuilder builder() {
        return new HttpResponseBuilder();
    }

    public String getHttpVersion() {
        return HTTP_VERSION;
    }

    public int getStatusCode() {
        return status.getStatusCode();
    }

    public String getStatusMessage() {
        return status.getStatusMessage();
    }

    public Optional<String> getHeader(String key) {
        return headers.getHeader(key);
    }

    public Map<String, String> getHeaders() {
        return headers.getHeaders();
    }

    public String getBody() {
        return body;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }
        HttpResponse that = (HttpResponse) object;
        return status == that.status && Objects.equals(headers, that.headers) && Objects.equals(body,
                that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, headers, body);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", HttpResponse.class.getSimpleName() + "[", "]")
                .add("status=" + status)
                .add("headers=" + headers)
                .add("body='" + body + "'")
                .toString();
    }

    public static class HttpResponseBuilder {
        private HttpStatus status;
        private Map<String, String> headers;
        private String body;

        private HttpResponseBuilder() {
            this.headers = new HashMap<>();
        }

        public HttpResponseBuilder ok() {
            status = HttpStatus.OK;
            return this;
        }

        public HttpResponseBuilder found() {
            status = HttpStatus.FOUND;
            return this;
        }

        public HttpResponseBuilder setHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }

        public HttpResponseBuilder contentType(String type) {
            headers.put("Content-Type", type);
            return this;
        }

        public HttpResponseBuilder body(String body) {
            this.body = body;
            return this;
        }

        public HttpResponse build() {
            setContentLength();
            return new HttpResponse(status, new HttpResponseHeaders(headers), body);
        }

        private void setContentLength() {
            int contentLength = body.getBytes(StandardCharsets.UTF_8).length;
            headers.put("Content-Length", String.valueOf(contentLength));
        }
    }
}
