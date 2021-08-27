package nextstep.jwp.framework.http;

public class HttpRequest {
    private final HttpMethod method;
    private final String uri;
    private final String version;
    private final HttpHeaders headers;
    private final String requestBody;

    public HttpRequest(HttpMethod method, String uri, String version, HttpHeaders headers, String requestBody) {
        this.method = method;
        this.uri = uri;
        this.version = version;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getVersion() {
        return version;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}
