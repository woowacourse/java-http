package org.apache.coyote.http11.message.request;

import java.net.URI;

public class HttpRequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String SPACE = " ";

    private final HttpMethod method;
    private final URI uri;
    private final String httpVersion;

    public HttpRequestLine(String requestLine) {
        this(
                parseMethod(requestLine),
                parsePath(requestLine),
                parseHttpVersion(requestLine)
        );
    }

    public HttpRequestLine(HttpMethod method, URI uri, String httpVersion) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    private static HttpMethod parseMethod(String startLine) {
        String[] parts = parseStartLine(startLine);
        return HttpMethod.valueOf(parts[METHOD_INDEX].toUpperCase());
    }

    private static URI parsePath(String startLine) {
        String[] parts = parseStartLine(startLine);
        return URI.create(parts[PATH_INDEX]);
    }

    private static String parseHttpVersion(String startLine) {
        String[] parts = parseStartLine(startLine);
        return parts[HTTP_VERSION_INDEX];
    }

    private static String[] parseStartLine(String startLine) {
        return startLine.split(SPACE);
    }

    public boolean hasPath(String path) {
        return uri.getPath().equals(path);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public URI getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
