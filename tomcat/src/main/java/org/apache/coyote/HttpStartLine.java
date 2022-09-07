package org.apache.coyote;

import org.apache.coyote.constant.HttpMethod;

public class HttpStartLine {

    private static final int HTTP_METHOD = 0;
    private static final int PATH = 1;

    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod method;
    private final HttpPath path;

    private HttpStartLine(final HttpMethod method, final HttpPath path) {
        this.method = method;
        this.path = path;
    }

    public static HttpStartLine from(final String startLine) {
        final String[] startLineContents = startLine.split(START_LINE_DELIMITER);

        final HttpMethod httpMethod = HttpMethod.from(startLineContents[HTTP_METHOD]);
        final HttpPath httpPath = HttpPath.from(startLineContents[PATH]);

        return new HttpStartLine(httpMethod, httpPath);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path.getValue();
    }

    public boolean isResource() {
        return path.isResource();
    }

    public String getParam(final String param) {
        return path.getParam(param);
    }

    @Override
    public String toString() {
        return "HttpStartLine{" +
                "method=" + method +
                ", path=" + path +
                '}';
    }
}
