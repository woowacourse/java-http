package org.apache.coyote.http11.request;

import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpVersion;

public class RequestLine {

    private final HttpMethod method;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod method, RequestUri requestUri, HttpVersion httpVersion) {
        this.method = method;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine parse(String requestLine) {
        String[] splitRequest = requestLine.split(" ");

        HttpMethod method = HttpMethod.of(splitRequest[0]);
        RequestUri requestURI = RequestUri.of(splitRequest[1]);
        HttpVersion httpVersion = HttpVersion.of(splitRequest[2]);

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
