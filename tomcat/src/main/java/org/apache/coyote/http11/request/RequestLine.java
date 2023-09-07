package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpVersion;

public class RequestLine {

    private static final String REQUEST_LINE_DELIMITER = " ";
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int HTTP_VERSION_INDEX = 2;

    private final HttpMethod method;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod method, RequestUri requestUri, HttpVersion httpVersion) {
        this.method = method;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(String requestLine) {
        String[] splitRequest = requestLine.split(REQUEST_LINE_DELIMITER);

        HttpMethod method = HttpMethod.of(splitRequest[METHOD_INDEX]);
        RequestUri requestURI = RequestUri.of(splitRequest[URI_INDEX]);
        HttpVersion httpVersion = HttpVersion.of(splitRequest[HTTP_VERSION_INDEX]);

        return new RequestLine(method, requestURI, httpVersion);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getRequestUri() {
        return requestUri.getUri();
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public String getQueryParam(String key) {
        return requestUri.getRequestParam(key);
    }

    public boolean existsQueryParam() {
        return requestUri.existsQueryParam();
    }

    public boolean isMatchMethod(HttpMethod httpMethod) {
        return method == httpMethod;
    }

    @Override
    public String toString() {
        return "RequestLine{" +
                "method=" + method +
                ", requestUri='" + requestUri + '\'' +
                ", httpVersion=" + httpVersion +
                '}';
    }
}
