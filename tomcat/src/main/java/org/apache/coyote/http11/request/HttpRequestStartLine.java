package org.apache.coyote.http11.request;

public class HttpRequestStartLine {

    private static final String START_LINE_DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpRequestMethod httpRequestMethod;
    private final HttpRequestUri httpRequestUri;
    private final String httpVersion;

    private HttpRequestStartLine(final HttpRequestMethod httpRequestMethod, final HttpRequestUri httpRequestUri,
                                 final String httpVersion) {
        this.httpRequestMethod = httpRequestMethod;
        this.httpRequestUri = httpRequestUri;
        this.httpVersion = httpVersion;
    }

    public static HttpRequestStartLine from(final String startLine) {
        final String[] startLineInfo = startLine.split(START_LINE_DELIMITER);

        final HttpRequestMethod httpRequestMethod = HttpRequestMethod.from(startLineInfo[METHOD_INDEX]);
        final HttpRequestUri requestUri = HttpRequestUri.from(startLineInfo[URI_INDEX]);
        final String httpVersion = startLineInfo[VERSION_INDEX];

        return new HttpRequestStartLine(httpRequestMethod, requestUri, httpVersion);
    }

    public HttpRequestMethod getHttpMethod() {
        return httpRequestMethod;
    }

    public HttpRequestUri getHttpRequestUri() {
        return httpRequestUri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
