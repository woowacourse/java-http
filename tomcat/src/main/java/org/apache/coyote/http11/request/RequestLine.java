package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpVersion;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int INDEX_OF_HTTP_METHOD = 0;
    private static final int INDEX_OF_URI = 1;
    private static final int INDEX_OF_HTTP_VERSION = 2;

    private final HttpMethod httpMethod;
    private final RequestUri uri;
    private final HttpVersion httpVersion;

    public RequestLine(String requestLine) {
        String[] parts = requestLine.split(REQUEST_LINE_DELIMITER);
        this.httpMethod = HttpMethod.from(parts[INDEX_OF_HTTP_METHOD]);
        this.uri = new RequestUri(parts[INDEX_OF_URI]);
        this.httpVersion = HttpVersion.from(parts[INDEX_OF_HTTP_VERSION]);
    }

    public boolean isGetMethod() {
        return httpMethod.isGet();
    }

    public boolean isPostMethod() {
        return httpMethod.isPost();
    }

    public boolean hasQueryParameter() {
        return uri.hasQueryParameter();
    }

    public String getPath() {
        return uri.getPath();
    }

    public String getQueryParameterAttribute(String name) {
        return uri.getQueryParameterAttribute(name);
    }

    public String getUri() {
        return uri.getUri();
    }
}
