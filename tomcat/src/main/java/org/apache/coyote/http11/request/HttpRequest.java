package org.apache.coyote.http11.request;

public class HttpRequest {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int HTTP_PATH_INDEX = 1;

    private final HttpMethod httpMethod;
    private final Path path;

    private HttpRequest(final HttpMethod httpMethod, final Path path) {
        this.httpMethod = httpMethod;
        this.path = path;
    }

    public static HttpRequest from(final String request) {
        String[] requestSegment = request.split(" ");
        return new HttpRequest(
                HttpMethod.from(requestSegment[HTTP_METHOD_INDEX]),
                Path.parsePath(requestSegment[HTTP_PATH_INDEX])
        );
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Path getPath() {
        return path;
    }
}
