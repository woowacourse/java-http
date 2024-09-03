package org.apache.coyote.http11;

public class Http11StartLine {

    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_TARGET_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final String HTTP_VERSION = "HTTP/1.1";

    private final HttpMethod method;
    private final RequestTarget requestTarget;

    private Http11StartLine(HttpMethod method, RequestTarget requestTarget) {
        this.method = method;
        this.requestTarget = requestTarget;
    }

    public static Http11StartLine from(String value) {
        String[] values = value.split(" ");

        validateVersion(values[HTTP_VERSION_INDEX]);

        HttpMethod method = HttpMethod.valueOf(values[HTTP_METHOD_INDEX]);
        RequestTarget requestTarget = RequestTarget.from(values[REQUEST_TARGET_INDEX]);

        return new Http11StartLine(method, requestTarget);
    }

    private static void validateVersion(String version) {
        if (!HTTP_VERSION.equals(version)) {
            throw new IllegalArgumentException("잘못된 HTTP 버전입니다.");
        }
    }

    public String getEndPoint() {
        return requestTarget.getEndPoint();
    }

    public HttpMethod getMethod() {
        return method;
    }

    public RequestTarget getRequestTarget() {
        return requestTarget;
    }

    @Override
    public String toString() {
        return "Http11StartLine{" +
               "method=" + method +
               ", requestTarget=" + requestTarget +
               '}';
    }
}
