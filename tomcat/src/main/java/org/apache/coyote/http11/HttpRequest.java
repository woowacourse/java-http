package org.apache.coyote.http11;

public class HttpRequest {

    private static final String REQUEST_API_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String POST = "POST";
    private static final String GET = "GET";
    private static final String PUT = "PUT";
    private static final String DELETE = "DELETE";
    private static final String REG_DOT = "\\.";
    private static final int EXTENSION_INDEX = 1;

    private final String method;
    private final String uri;
    private final String version;

    public HttpRequest(final String method, final String uri, final String version) {
        validateMethod(method);
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    private void validateMethod(final String method) {
        if (method.equalsIgnoreCase(POST) ||
                method.equalsIgnoreCase(GET) ||
                method.equalsIgnoreCase(PUT) ||
                method.equalsIgnoreCase(DELETE)) {
            return;
        }
        throw new IllegalArgumentException("지원하지 않는 HTTP METHOD 입니다.");
    }

    public static HttpRequest of(String requestInfo) {
        final String[] httpRequest = requestInfo.split(REQUEST_API_DELIMITER);
        if (httpRequest.length != 3) {
            throw new IllegalArgumentException("잘못된 http 요청 입니다.");
        }
        return new HttpRequest(httpRequest[HTTP_METHOD_INDEX], httpRequest[REQUEST_URI_INDEX],
                httpRequest[HTTP_VERSION_INDEX]);
    }

    public static HttpRequest toIndex() {
        return new HttpRequest(GET, "/index.html", "HTTP/1.1");
    }

    public String getUri() {
        return uri;
    }

    public boolean isGet() {
        return method.equalsIgnoreCase(GET);
    }

    public String getExtension() {
        return uri.split(REG_DOT)[EXTENSION_INDEX];
    }
}
