package nextstep.jwp.framework.http;

public class RequestLine {
    private final HttpMethod method;
    private final String uri;
    private final String version;

    public RequestLine(HttpMethod method, String uri, String version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
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
}
