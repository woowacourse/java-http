package org.apache.coyote.http11;

import org.apache.coyote.http11.request.model.HttpVersion;

public class HttpResponse {

    private final String value;

    public HttpResponse(final String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String body;
        private HttpVersion version = HttpVersion.HTTP_1_1;
        private String status;
        private String[] headers;

        public Builder body(final String body) {
            this.body = body;
            return this;
        }

        public Builder version(final HttpVersion version) {
            this.version = version;
            return this;
        }

        public Builder status(final String status) {
            this.status = status;
            return this;
        }

        public Builder headers(final String... headers) {
            this.headers = headers;
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(
                    String.join("\r\n",
                            version.getValue() + " " + status + " ",
                            String.join(" \r\n", headers) + " \r\n",
                            body == null ? "" : body));
        }
    }
}
