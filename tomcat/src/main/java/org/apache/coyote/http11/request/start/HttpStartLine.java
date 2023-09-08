package org.apache.coyote.http11.request.start;

public class HttpStartLine {
    public static final int HTTP_METHOD_INDEX = 0;
    public static final int REQUEST_TARGET_INDEX = 1;
    public static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final HttpVersion httpVersion;

    private HttpStartLine(final HttpMethod httpMethod, final RequestTarget requestTarget, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
    }


    public static HttpStartLine from(final String httpStartLine) {
        final String[] splitRequestLines = httpStartLine.split(" ");
        validateHttpRequestLine(splitRequestLines);

        return new HttpStartLine(
                HttpMethod.from(splitRequestLines[HTTP_METHOD_INDEX]),
                RequestTarget.from(validateDefault(splitRequestLines[REQUEST_TARGET_INDEX])),
                HttpVersion.from(splitRequestLines[HTTP_VERSION_INDEX])
        );
    }

    private static String validateDefault(final String requestTarget) {
        if (requestTarget.equals("/")) {
            return "default.html";
        }
        return requestTarget;
    }

    private static void validateHttpRequestLine(final String[] splitRequestLines) {
        if (splitRequestLines.length != 3) {
            throw new IllegalArgumentException("잘못된 http요청입니다.");
        }
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestTarget getRequestTarget() {
        return requestTarget;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }
}
