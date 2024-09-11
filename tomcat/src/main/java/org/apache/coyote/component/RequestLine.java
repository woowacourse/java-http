package org.apache.coyote.component;

import java.net.URL;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final String REQUEST_SEPARATOR = " ";
    private static final int PROTOCOL_INDEX = 2;

    private final HttpMethod httpMethod;
    private final Path path;
    private final Protocol protocol;

    public RequestLine(final HttpMethod httpMethod, final Path path, final Protocol protocol) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.protocol = protocol;
    }

    public static RequestLine from(final String line) {
        final var texts = line.split(REQUEST_SEPARATOR);
        final var method = HttpMethod.fromName(texts[METHOD_INDEX]);
        final var path = new Path(texts[PATH_INDEX]);
        final var protocol = Protocol.from(texts[PROTOCOL_INDEX]);
        return new RequestLine(method, path, protocol);
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

    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
               "httpMethod=" + httpMethod +
               ", path=" + path +
               ", protocol=" + protocol +
               '}';
    }
}
