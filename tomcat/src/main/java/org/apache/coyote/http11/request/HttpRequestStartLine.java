package org.apache.coyote.http11.request;

public class HttpRequestStartLine {

    private static final String DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;
    private static final int START_LINE_TOKEN_SIZE = 3;

    private final HttpRequestMethod httpRequestMethod;
    private final String requestURI;
    private final String httpVersion;

    private HttpRequestStartLine(
            final HttpRequestMethod httpRequestMethod,
            final String requestURI,
            final String httpVersion
    ) {
        this.httpRequestMethod = httpRequestMethod;
        this.requestURI = requestURI;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestStartLine from(final String requestLine) {
        final String[] startLineTokens = requestLine.split(DELIMITER);
        validateStartLineTokenSize(startLineTokens);
        final HttpRequestMethod httpRequestMethod = HttpRequestMethod.valueOf(startLineTokens[HTTP_METHOD_INDEX]);
        final String requestURI = startLineTokens[REQUEST_URI_INDEX];
        final String httpVersion = startLineTokens[HTTP_VERSION_INDEX];
        return new HttpRequestStartLine(httpRequestMethod, requestURI, httpVersion);
    }

    private static void validateStartLineTokenSize(final String[] lines) {
        if (lines.length != START_LINE_TOKEN_SIZE) {
            throw new IllegalArgumentException("시작 라인의 토큰은 3개여야 합니다.");
        }
    }

    public HttpRequestMethod getHttpRequestMethod() {
        return httpRequestMethod;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
