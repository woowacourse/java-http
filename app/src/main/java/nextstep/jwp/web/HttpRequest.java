package nextstep.jwp.web;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final String requestBody;

    public HttpRequest(RequestLine requestLine,
                       Map<String, String> headers,
                       Map<String, String> parameters,
                       String requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.parameters = parameters;
        this.requestBody = requestBody;
    }

    public static HttpRequestBuilder builder() {
        return new HttpRequestBuilder();
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public URI getRequestUri() {
        return requestLine.getRequestUri();
    }

    public HttpVersion getHttpVersion() {
        return requestLine.getHttpVersion();
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
        private HttpVersion httpVersion;
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

        public HttpRequestBuilder httpVersion(HttpVersion httpVersion) {
            this.httpVersion = httpVersion;
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
            return new HttpRequest(new RequestLine(method, requestUri, httpVersion), headers, parameters, body);
        }
    }
}
