package nextstep.jwp.framework.http;

import java.util.Objects;

import nextstep.jwp.framework.http.formatter.RequestLineFormatter;

public class RequestLine {
    private final HttpMethod method;
    private final URI uri;
    private final HttpVersion version;

    public RequestLine(HttpMethod method, String uri, HttpVersion version) {
        this(method, new URI(uri), version);
    }

    public RequestLine(HttpMethod method, URI uri, HttpVersion version) {
        this.method = Objects.requireNonNull(method);
        this.uri = Objects.requireNonNull(uri);
        this.version = Objects.requireNonNull(version);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getVersion() {
        return version.getVersion();
    }

    public boolean isSamePath(String path) {
        return uri.isSamePath(path);
    }

    @Override
    public String toString() {
        return new RequestLineFormatter(this).transform();
    }
}
