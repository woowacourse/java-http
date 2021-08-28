package nextstep.joanne.domain;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private HttpMethod method;
    private URI uri;
    private Optional<String> versionOfProtocol;
    private HashMap<String, String> requestHeaders;
    private HashMap<String, String> requestMessageBody;

    private HttpRequest(Builder builder) {
        this.method = builder.method;
        this.uri = builder.uri;
        this.versionOfProtocol = builder.versionOfProtocol;
        this.requestHeaders = builder.requestHeaders;
        this.requestMessageBody = builder.requestMessageBody;
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
            this.uri = new URI(uri);
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

    public String contentType() {
        if (requestHeaders.containsKey("Accept") && requestHeaders.get("Accept").contains("css")) {
            return "text/css";
        }
        return "text/html";
    }

    public boolean uriEquals(String uri) {
        return this.uri.equalsWith(uri);
    }

    public String getFromRequestBody(String key) {
        return requestMessageBody.get(key);
    }

    public boolean isGet() {
        return method.sameWith("GET");
    }

    public boolean isPost() {
        return method.sameWith("POST");
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
