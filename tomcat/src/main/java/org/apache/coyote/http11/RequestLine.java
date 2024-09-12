package org.apache.coyote.http11;

public class RequestLine {

    private static final String START_LINE_DELIMITER = " ";
    private static final int FIRST = 0;
    private static final int SECOND = 1;
    private static final int THIRD = 2;

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod httpMethod, RequestUri requestUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String startLine) {
        String[] splitStartLine = startLine.split(START_LINE_DELIMITER);
        HttpMethod httpMethod = HttpMethod.from(splitStartLine[FIRST]);
        RequestUri requestUri = RequestUri.from(splitStartLine[SECOND]);
        HttpVersion httpVersion = HttpVersion.from(splitStartLine[THIRD]);

        return new RequestLine(httpMethod, requestUri, httpVersion);
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "httpMethod=" + httpMethod +
                ", requestUri=" + requestUri +
                ", httpVersion=" + httpVersion +
                '}';
    }
}
