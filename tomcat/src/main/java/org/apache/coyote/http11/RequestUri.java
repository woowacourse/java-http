package org.apache.coyote.http11;

public class RequestUri {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int HTTP_PROTOCOL_INDEX = 2;

    private final HttpMethod httpMethod;
    private final String path;
    private final HttpProtocol httpProtocol;

    public RequestUri(HttpMethod httpMethod, String path, HttpProtocol httpProtocol) {
        validate(httpMethod, path, httpProtocol);
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpProtocol = httpProtocol;
    }

    private void validate(HttpMethod httpMethod, String path, HttpProtocol httpProtocol) {
        if (httpMethod == null) {
            throw new IllegalArgumentException("HttpMethod는 null이 될 수 없습니다.");
        }
        if (path == null) {
            throw new IllegalArgumentException("Path는 null이 될 수 없습니다.");
        }
        if (httpProtocol == null) {
            throw new IllegalArgumentException("HttpProtocol은 null이 될 수 없습니다.");
        }
    }

    public static RequestUri from(String requestUri) {
        if (requestUri == null) {
            throw new IllegalArgumentException("requestUri는 null이 될 수 없습니다.");
        }
        String[] reqeustUriSegments = requestUri.split(" ");
        if (reqeustUriSegments.length != 3) {
            throw new IllegalArgumentException("지원되지 않는 RequestURI 입니다.");
        }
        String httpMethod = reqeustUriSegments[HTTP_METHOD_INDEX];
        String path = reqeustUriSegments[PATH_INDEX];
        String httpProtocol = reqeustUriSegments[HTTP_PROTOCOL_INDEX];
        return new RequestUri(HttpMethod.from(httpMethod), path, HttpProtocol.from(httpProtocol));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public HttpProtocol getHttpProtocol() {
        return httpProtocol;
    }
}
