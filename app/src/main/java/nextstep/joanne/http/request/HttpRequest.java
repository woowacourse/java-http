package nextstep.joanne.http.request;

import nextstep.joanne.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private final HttpMethod method;
    private final URI uri;
    private final Optional<String> versionOfProtocol;
    private final RequestHeaders requestHeaders;
    private final RequestMessageBody requestMessageBody;

    private HttpRequest(Builder builder) {
        this.method = builder.method;
        this.uri = builder.uri;
        this.versionOfProtocol = builder.versionOfProtocol;
        this.requestHeaders = new RequestHeaders(builder.requestHeaders);
        this.requestMessageBody = new RequestMessageBody(builder.requestMessageBody);
    }

    public static class Builder {
        private HttpMethod method;
        private URI uri;
        private Optional<String> versionOfProtocol;
        private HashMap<String, String> requestHeaders;
        private HashMap<String, String> requestMessageBody;

        public Builder() {
            versionOfProtocol = Optional.empty();
            requestHeaders = new HashMap<>();
            requestMessageBody = new HashMap<>();
        }

        public Builder method(String method) {
            this.method = HttpMethod.valueOf(method);
            return this;
        }

        public Builder uri(String uri) {
            this.uri = URI.of(uri);
            return this;
        }

        public Builder versionOfProtocol(String versionOfProtocol) {
            this.versionOfProtocol = Optional.of(versionOfProtocol);
            return this;
        }

        public Builder requestHeaders(Map<String, String> requestHeaders) {
            this.requestHeaders = (HashMap<String, String>) requestHeaders;
            return this;
        }

        public Builder requestMessageBody(Map<String, String> requestMessageBody) {
            this.requestMessageBody = (HashMap<String, String>) requestMessageBody;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }

    public boolean uriEquals(String uri) {
        return this.uri.equalsWith(uri);
    }

    public boolean uriContains(String uri) {
        return this.uri.contains(uri);
    }

    public String getFromRequestBody(String key) {
        return requestMessageBody.valueFromKey(key);
    }

    public boolean isEqualsMethod(HttpMethod httpMethod) {
        return method.sameWith(httpMethod);
    }

    public String resourceUri() {
        return uri.resourceUri();
    }

    public String getFromQueryString(String value) {
        return uri.queryString().get(value);
    }

    public Optional<String> getVersionOfProtocol() {
        return versionOfProtocol;
    }
}
