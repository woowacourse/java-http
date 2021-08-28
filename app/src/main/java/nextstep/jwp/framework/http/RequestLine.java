package nextstep.jwp.framework.http;

import java.util.Map;

public class RequestLine {
    private final HttpMethod method;
    private final URI uri;
    private final HttpVersion version;

    public RequestLine(HttpMethod method, String uri, HttpVersion version) {
        this.method = method;
        this.uri = new URI(uri);
        this.version = version;
    }

    public RequestLine(HttpMethod method, URI uri, HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getVersion() {
        return version.name();
    }

    public boolean isSamePath(String path) {
        return uri.isSamePath(path);
    }

    public Map<String, String> getQueries() {
        return uri.getQueries();
    }
}
