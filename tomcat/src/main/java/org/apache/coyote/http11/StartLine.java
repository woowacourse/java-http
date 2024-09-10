package org.apache.coyote.http11;

import java.util.Map;

public class StartLine {

    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private StartLine(HttpMethod httpMethod, RequestUri requestUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static StartLine from(String startLine) {
        String[] splitStartLine = startLine.split(START_LINE_DELIMITER);
        HttpMethod httpMethod = HttpMethod.from(splitStartLine[0]);
        RequestUri requestUri = RequestUri.from(splitStartLine[1]);
        HttpVersion httpVersion = HttpVersion.from(splitStartLine[2]);

        return new StartLine(httpMethod, requestUri, httpVersion);
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

    public Map<String, String> getQueryParameters() {
        return requestUri.getQueryParameters();
    }
}
