package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpVersion;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod httpMethod, RequestUri requestUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(String requestLine) {
        String[] requestLineParts = requestLine.split(REQUEST_LINE_DELIMITER);

        HttpMethod httpMethod = HttpMethod.valueOf(requestLineParts[0].toUpperCase());
        RequestUri requestUri = RequestUri.from(requestLineParts[1]);
        HttpVersion httpVersion = HttpVersion.from(requestLineParts[2]);

        return new RequestLine(httpMethod, requestUri, httpVersion);
    }

    public boolean isSameHttpMethod(HttpMethod method) {
        return httpMethod.equals(method);
    }

    public boolean isQueryStringExisted() {
        return requestUri.isQueryStringExisted();
    }

    public String findQueryStringValue(String key) {
        return requestUri.findQueryStringValue(key);
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

}
