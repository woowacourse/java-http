package org.apache.coyote.http11;

public class HttpStartLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String SPACE = " ";

    private final HttpMethod method;
    private final String path;
    private final String httpVersion;


    public HttpStartLine(String startLine) {
        this.method = parseMethod(startLine);
        this.path = parsePath(startLine);
        this.httpVersion = parseHttpVersion(startLine);
    }

    private HttpMethod parseMethod(String startLine) {
        String[] parts = parseStartLine(startLine);
        return HttpMethod.valueOf(parts[METHOD_INDEX].toUpperCase());
    }

    private String parsePath(String startLine) {
        String[] parts = parseStartLine(startLine);
        return parts[PATH_INDEX];
    }

    private String parseHttpVersion(String startLine) {
        String[] parts = parseStartLine(startLine);
        return parts[HTTP_VERSION_INDEX];
    }

    private String[] parseStartLine(String startLine) {
        return startLine.split(SPACE);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
