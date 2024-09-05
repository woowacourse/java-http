package org.apache.coyote.request;

import org.apache.coyote.request.uri.URI;

public class RequestLine {

    private static final String DELIMITER = " ";
    private static final int VALID_SPLIT_REQUEST_LINE_LENGTH = 3;

    private final HttpMethod httpMethod;

    private final URI uri;

    private final String httpVersion;

    protected RequestLine(String requestLine) {
        String[] requestLines = requestLine.split(DELIMITER);
        validLength(requestLines);

        this.httpMethod = HttpMethod.from(requestLines[0]);
        this.uri = new URI(requestLines[1]);
        this.httpVersion = requestLines[2];
    }

    private void validLength(String[] requestLines) {
        if (requestLines.length != VALID_SPLIT_REQUEST_LINE_LENGTH) {
            throw new IllegalArgumentException("잘못된 Request line입니다.");
        }
    }

    protected HttpMethod getHttpMethod() {
        return this.httpMethod;
    }

    protected String getPath() {
        return uri.getPath();
    }

    protected String getQueryParamValue(String key) {
        return uri.getQueryParamValue(key);
    }

    protected String getHttpVersion() {
        return this.httpVersion;
    }
}
