package org.apache.coyote.http11.message.request;

public class HttpRequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String SPACE = " ";

    private final HttpMethod method;
    private final String uri;
    private final String httpVersion;

    public HttpRequestLine(HttpMethod method, String uri, String httpVersion) {
        this.method = method;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public HttpRequestLine(String startLine) {
        this(
                parseMethod(startLine),
                parsePath(startLine),
                parseHttpVersion(startLine)
        );
    }

    private static HttpMethod parseMethod(String startLine) {
        String[] parts = parseStartLine(startLine);
        return HttpMethod.valueOf(parts[METHOD_INDEX].toUpperCase());
    }

    private static String parsePath(String startLine) {
        String[] parts = parseStartLine(startLine);
        return parts[PATH_INDEX];
    }

    private static String parseHttpVersion(String startLine) {
        String[] parts = parseStartLine(startLine);
        return parts[HTTP_VERSION_INDEX];
    }

    private static String[] parseStartLine(String startLine) {
        return startLine.split(SPACE);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
