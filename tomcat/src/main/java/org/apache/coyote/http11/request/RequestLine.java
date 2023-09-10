package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpVersion;

import java.util.Optional;

public class RequestLine {

    private static final String SEPARATOR = " ";
    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final Method method;
    private final Path path;
    private final HttpVersion version;

    private RequestLine(final Method method, final Path path, final HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
    }

    public static RequestLine from(final String request) {
        final String[] requestLine = request.split(SEPARATOR);
        final Method method = Method.findBy(requestLine[METHOD_INDEX]);
        final Path path = Path.from(requestLine[PATH_INDEX]);
        final HttpVersion httpVersion = HttpVersion.findBy(requestLine[VERSION_INDEX]);

        return new RequestLine(method, path, httpVersion);
    }

    public boolean isSameMethod(final Method method) {
        return this.method.equals(method);
    }

    public boolean isSameUri(final String uri) {
        return this.path.isSameUri(uri);
    }

    public Method getMethod() {
        return method;
    }

    public String getUri() {
        return path.getUri();
    }

    public Optional<QueryString> getQueryString() {
        return Optional.ofNullable(path.getQueryString());
    }

    public HttpVersion getVersion() {
        return version;
    }
}
