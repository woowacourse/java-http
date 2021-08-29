package nextstep.jwp.model;

import java.util.Map;

public class CustomHttpRequest {
    private final String method;
    private final String uri;
    private final String httpVersion;
    private final Map<String, String> headers;
    private final Map<String, String> params;

    public CustomHttpRequest(String method, String uri, String httpVersion,
                             Map<String, String> headers, Map<String, String> params) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.headers = headers;
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getParams() {
        return params;
    }
}
