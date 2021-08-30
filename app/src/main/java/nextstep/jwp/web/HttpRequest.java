package nextstep.jwp.web;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private final HttpMethod method;
    private final URI requestUri;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String requestBody;

    public HttpRequest(HttpMethod method,
                       URI requestUri,
                       Map<String, String> headers,
                       Map<String, String> parameters,
                       String requestBody) {
        this.method = method;
        this.requestUri = requestUri;
        this.headers = headers;
        this.parameters = parameters;
        this.requestBody = requestBody;
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getRequestUri() {
        return requestUri;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getParameter(String parameter) {
        return this.parameters.get(parameter);
    }

    public static class HttpRequestBuilder {
        private HttpMethod method;
        private URI requestUri;
        private Map<String, String> headers = new HashMap<>();
        private Map<String, String> parameters = new HashMap<>();
        private String body;

        public HttpRequestBuilder method(HttpMethod method) {
            this.method = method;
            return this;
        }

        public HttpRequestBuilder requestUri(URI uri) {
            this.requestUri = uri;
            return this;
        }

        public HttpRequestBuilder headers(Map<String, String> headers) {
            this.headers.putAll(headers);
            return this;
        }

        public HttpRequestBuilder parameters(Map<String, String> parameters) {
            this.parameters.putAll(parameters);
            return this;
        }

        public HttpRequestBuilder body(String body) {
            this.body = body;
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(method, requestUri, headers, parameters, body);
        }
    }
}
