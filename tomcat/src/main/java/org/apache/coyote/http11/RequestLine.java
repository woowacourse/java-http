package org.apache.coyote.http11;

public class RequestLine {

    private static final String START_LINE_DELIMITER = " ";
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int REQUEST_URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

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
        HttpMethod httpMethod = HttpMethod.from(splitStartLine[HTTP_METHOD_INDEX]);
        RequestUri requestUri = RequestUri.from(splitStartLine[REQUEST_URI_INDEX]);
        HttpVersion httpVersion = HttpVersion.from(splitStartLine[HTTP_VERSION_INDEX]);

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
