package org.apache.coyote.common;

public class RequestUri {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int HTTP_PROTOCOL_INDEX = 2;

    private final HttpMethod httpMethod;
    private final HttpPath httpPath;
    private final HttpProtocol httpProtocol;

    private RequestUri(HttpMethod httpMethod, HttpPath httpPath, HttpProtocol httpProtocol) {
        validate(httpMethod, httpPath, httpProtocol);
        this.httpMethod = httpMethod;
        this.httpPath = httpPath;
        this.httpProtocol = httpProtocol;
    }

    private void validate(HttpMethod httpMethod, HttpPath path, HttpProtocol httpProtocol) {
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
        return new RequestUri(HttpMethod.from(httpMethod), HttpPath.from(path), HttpProtocol.from(httpProtocol));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpPath getHttpPath() {
        return httpPath;
    }

    public HttpProtocol getHttpProtocol() {
        return httpProtocol;
    }
}
