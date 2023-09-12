package org.apache.coyote.http11.message.request;

import java.util.Objects;
import org.apache.coyote.http11.message.HttpMethod;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_AND_PARAMETER_INDEX = 1;
    private static final int PROTOCOL_INDEX = 2;
    private static final String DEFAULT_PROTOCOL = "HTTP/1.1";

    private final HttpMethod method;
    private final String path;
    private final String protocol;
    private final QueryStrings queryStrings;

    public RequestLine(HttpMethod method, String path) {
        this(method, path, DEFAULT_PROTOCOL, QueryStrings.empty());
    }

    public RequestLine(HttpMethod method, String path, String protocol, QueryStrings queryStrings) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.queryStrings = queryStrings;
    }

    public static RequestLine from(String[] uri) {
        final var method = HttpMethod.of(uri[METHOD_INDEX]);
        final var fullPath = uri[PATH_AND_PARAMETER_INDEX];
        final var protocol = uri[PROTOCOL_INDEX];
        final var queryStrings = new QueryStrings(fullPath);
        final var path = fullPath.split("\\?")[0];
        return new RequestLine(method, path, protocol, queryStrings);
    }

    public boolean isPathMatch(String path) {
        return this.path.equals(path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RequestLine that = (RequestLine) o;
        return method == that.method && Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path);
    }
}
