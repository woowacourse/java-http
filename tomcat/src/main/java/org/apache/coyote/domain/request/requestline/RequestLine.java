package org.apache.coyote.domain.request.requestline;

public class RequestLine {

    private static final String HEADER_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final Path path;
    private final HttpVersion httpVersion;

    public RequestLine(HttpMethod httpMethod, Path path, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        final String[] requestLines = requestLine.split(HEADER_DELIMITER);
        final HttpMethod httpMethod = HttpMethod.get(requestLines[HTTP_METHOD_INDEX]);
        final Path path = Path.from(requestLines[PATH_INDEX]);
        final HttpVersion httpVersion = HttpVersion.from(requestLines[HTTP_VERSION_INDEX]);
        return new RequestLine(httpMethod, path, httpVersion);
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Path getPath() {
        return path;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
