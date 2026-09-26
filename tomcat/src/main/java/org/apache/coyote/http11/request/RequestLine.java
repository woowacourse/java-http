package org.apache.coyote.http11.request;

public class RequestLine {

    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String TOKEN_SEPARATOR = " ";
    private static final String PROTOCOL_SEPARATOR = "/";
    private static final String QUERY_SEPARATOR = "?";

    private final String method;
    private final String path;
    private final String protocol;
    private final String version;

    private RequestLine(final String method, final String path,
                        final String protocol, final String version) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.version = version;
    }

    public static RequestLine from(final String requestLine) {
        final String[] tokens = requestLine.trim().split(TOKEN_SEPARATOR);
        if (tokens.length != 3) {
            throw new IllegalArgumentException("올바르지 않은 request line 입니다. " + requestLine);
        }
        final String[] protocolAndVersion = parseProtocol(tokens[2]);
        return new RequestLine(tokens[0], parsePath(tokens[1]), protocolAndVersion[0], protocolAndVersion[1]);
    }

    private static String parsePath(final String uri) {
        final int index = uri.indexOf(QUERY_SEPARATOR);
        if (index == -1) {
            return uri;
        }
        return uri.substring(0, index);
    }

    private static String[] parseProtocol(final String token) {
        final String[] protocolAndVersion = token.split(PROTOCOL_SEPARATOR);
        if (protocolAndVersion.length != 2) {
            throw new IllegalArgumentException("올바르지 않은 프로토콜 입니다. " + token);
        }
        return protocolAndVersion;
    }

    public boolean isGet() {
        return method.equals(GET);
    }

    public boolean isPost() {
        return method.equals(POST);
    }

    public boolean matchesPath(final String other) {
        return path.equals(other);
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getVersion() {
        return version;
    }
}
