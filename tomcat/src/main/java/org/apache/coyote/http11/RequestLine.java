package org.apache.coyote.http11;

import java.net.URL;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final String REQUEST_SEPARATOR = " ";

    private final HttpMethod httpMethod;
    private final Path path;

    private RequestLine(final HttpMethod httpMethod, final Path path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public static RequestLine from(final String line) {
        final var texts = line.split(REQUEST_SEPARATOR);
        final var method = HttpMethod.fromName(texts[METHOD_INDEX]);
        final var path = new Path(texts[PATH_INDEX]);
        return new RequestLine(method, path);
    }

    public boolean isEqualHttpMethod(final HttpMethod target) {
        return httpMethod == target;
    }

    public URL getAbsolutePath() {
        return path.getAbsolutePath();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
               "httpMethod=" + httpMethod +
               ", path=" + path.getRequestPath() +
               '}';
    }
}
