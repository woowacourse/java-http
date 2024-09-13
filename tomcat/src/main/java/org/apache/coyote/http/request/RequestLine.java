package org.apache.coyote.http.request;

import org.apache.coyote.http.request.uri.URI;
import org.apache.coyote.http.HttpMethod;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int VALID_SPLIT_REQUEST_LINE_LENGTH = 3;
    private static final int HTTP_METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod httpMethod;

    private final URI uri;

    private final String httpVersion;

    public RequestLine(String requestLine) {
        validateRequestLine(requestLine);
        String[] requestLines = split(requestLine);
        this.httpMethod = HttpMethod.from(requestLines[HTTP_METHOD_INDEX]);
        this.uri = new URI(requestLines[URI_INDEX]);
        this.httpVersion = requestLines[HTTP_VERSION_INDEX];
    }

    private void validateRequestLine(String requestLine) {
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Request line은 필수입니다.");
        }
    }

    private String[] split(String requestLine) {
        String[] requestLines = requestLine.split(REQUEST_LINE_DELIMITER);
        if (requestLines.length != VALID_SPLIT_REQUEST_LINE_LENGTH) {
            throw new IllegalArgumentException("잘못된 Request line입니다. request line: '%s'".formatted(requestLine));
        }
        return requestLines;
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

    protected boolean existQueryParams() {
        return uri.existQueryParams();
    }
}
