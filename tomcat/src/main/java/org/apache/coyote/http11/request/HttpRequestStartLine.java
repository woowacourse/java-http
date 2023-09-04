package org.apache.coyote.http11.request;

public class HttpRequestStartLine {

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
        String[] startLineInfo = startLine.split(" ");

        HttpRequestMethod httpRequestMethod = HttpRequestMethod.from(startLineInfo[0]);
        HttpRequestUri requestUri = HttpRequestUri.from(startLineInfo[1]);
        String httpVersion = startLineInfo[2];

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
