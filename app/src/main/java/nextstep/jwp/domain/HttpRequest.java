package nextstep.jwp.domain;

import nextstep.jwp.infra.HttpBodyBuilder;
import nextstep.jwp.infra.HttpHeaderBuilder;

import java.util.Optional;

public class HttpRequest {

    private HttpMethod method;
    private URI uri;
    private HttpHeaderBuilder headerBuilder;
    private HttpHeaders httpHeaders;
    private HttpBodyBuilder bodyBuilder;
    private HttpBodies httpBodies;
    private Optional<String> version;

    public HttpRequest(Builder builder) {
        method = builder.method;
        uri = builder.uri;
        headerBuilder = builder.headerBuilder;
        bodyBuilder = builder.bodyBuilder;
        version = builder.version;
    }

    public static class Builder {
        private HttpMethod method;
        private URI uri;
        private HttpHeaderBuilder headerBuilder;
        private HttpHeaders httpHeaders;
        private HttpBodyBuilder bodyBuilder;
        private Optional<String> version;

        public Builder() {
            headerBuilder = new HttpHeaderBuilder();
            bodyBuilder = new HttpBodyBuilder();
            version = Optional.empty();
        }

        public Builder method(String method) {
            this.method = HttpMethod.valueOf(method);
            return this;
        }

        public Builder uri(String uri) {
            this.uri = new URI(uri);
            return this;
        }

        public Builder requestHeaders(String[] splitRequestHeaders) {
            this.httpHeaders = headerBuilder.addHeader(splitRequestHeaders).build();
            return this;
        }

        public Builder bodyBuilder(HttpBodyBuilder bodyBuilder) {
            this.bodyBuilder = bodyBuilder;
            return this;
        }

        public Builder version(String version) {
            this.version = Optional.of(version);
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}
