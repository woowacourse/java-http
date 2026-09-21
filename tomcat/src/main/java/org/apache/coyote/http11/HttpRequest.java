package org.apache.coyote.http11;

import java.util.Map;
import java.util.Objects;

public record HttpRequest(
        HttpMethod httpMethod,
        String path,
        String version,
        Map<String, String> headers,
        Map<String, String> params,
        String body
) {
    public HttpRequest {
        Objects.requireNonNull(httpMethod, "httpMethod는 null일 수 없습니다.");
        Objects.requireNonNull(path, "path는 null일 수 없습니다.");
        Objects.requireNonNull(version, "version은 null일 수 없습니다.");
        Objects.requireNonNull(headers, "headers는 null일 수 없습니다.");
        Objects.requireNonNull(params, "params는 null일 수 없습니다.");
        Objects.requireNonNull(body, "body는 null일 수 없습니다.");

        headers = Map.copyOf(headers);
        params = Map.copyOf(params);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private HttpMethod httpMethod;
        private String path;
        private String version;
        private Map<String, String> headers = Map.of();
        private Map<String, String> params = Map.of();
        private String body = "";

        public Builder httpMethod(final HttpMethod httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        public Builder path(final String path) {
            this.path = path;
            return this;
        }

        public Builder version(final String version) {
            this.version = version;
            return this;
        }

        public Builder headers(final Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder params(final Map<String, String> params) {
            this.params = params;
            return this;
        }

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(
                    httpMethod,
                    path,
                    version,
                    headers,
                    params,
                    body
            );
        }
    }
}
