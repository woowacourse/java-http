package org.apache.coyote.http.vo;

import org.apache.coyote.http.HttpMethod;

public class HttpRequest {

    private final HttpMethod method;
    private final Url url;
    private final HttpHeaders headers;

    private HttpRequest(
            final HttpMethod method,
            final Url url,
            final HttpHeaders headers
    ) {
        this.method = method;
        this.url = url;
        this.headers = headers;
    }

    public boolean isContainsSubStringInUrl(final String subString) {
        return url.isContainSubString(subString);
    }

    public boolean isRequestMethodOf(final HttpMethod method) {
        return this.method == method;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public Url getUrl() {
        return url;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public static class Builder {
        private HttpMethod method;
        private Url url;
        private HttpHeaders headers;

        public Builder httpMethod(final HttpMethod method) {
            this.method = method;
            return this;
        }

        public Builder url(final Url url) {
            this.url = url;
            return this;
        }

        public Builder headers(final HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(method, url, headers);
        }
    }
}
