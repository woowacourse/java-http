package org.apache.coyote.http11;

public class StartLine {

    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    public StartLine(HttpMethod httpMethod, RequestUri requestUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static StartLine from(String startLine) {
        String[] splitStartLine = startLine.split(START_LINE_DELIMITER);
        String httpMethod = splitStartLine[0];
        String requestUri = splitStartLine[1];
        String httpVersion = splitStartLine[2];

        return new StartLine(HttpMethod.from(httpMethod), RequestUri.from(requestUri), HttpVersion.from(httpVersion));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestUri() {
        return requestUri.getRequestUri();
    }

    public String getHttpVersion() {
        return httpVersion.toString();
    }
}
