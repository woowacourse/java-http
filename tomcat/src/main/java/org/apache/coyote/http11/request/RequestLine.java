package org.apache.coyote.http11.request;

import java.util.Map;
import org.apache.coyote.http11.common.HttpVersion;

public class RequestLine {

    private static final String SPLIT_DELIMITER = " ";
    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(final HttpMethod httpMethod, final RequestUri requestUri, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String startLine) {
        String[] startLineElements = startLine.split(SPLIT_DELIMITER);
        HttpMethod httpMethod = HttpMethod.findHttpMethod(startLineElements[0]);
        RequestUri requestUri = new RequestUri(startLineElements[1]);
        HttpVersion httpVersion = new HttpVersion(startLineElements[2].trim());
        return new RequestLine(httpMethod, requestUri, httpVersion);
    }

    public Map<String, Object> getQueryParams() {
        return requestUri.parseQueryParams();
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
