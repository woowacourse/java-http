package org.apache.coyote.http11.request;

public class RequestStartLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_PATH_INDEX = 1;
    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final Path path;

    private RequestStartLine(final HttpMethod httpMethod, final Path path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public static RequestStartLine from(final String request) {
        String[] requestSegment = request.split(START_LINE_DELIMITER);
        return new RequestStartLine(
                HttpMethod.from(requestSegment[HTTP_METHOD_INDEX]),
                Path.parsePath(requestSegment[HTTP_PATH_INDEX])
        );
    }

    public boolean isGet() {
        return httpMethod.equals(HttpMethod.GET);
    }

    public boolean isPost() {
        return httpMethod.equals(HttpMethod.POST);
    }

    public Path getPath() {
        return path;
    }
}
