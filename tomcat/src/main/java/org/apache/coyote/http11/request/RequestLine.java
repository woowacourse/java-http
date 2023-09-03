package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;

public class RequestLine {
    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(final HttpMethod httpMethod,
                        final RequestUri requestUri,
                        final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String line) {
        final String[] split = line.split(" ");
        return new RequestLine(
                HttpMethod.valueOf(split[0]),
                RequestUri.from(split[1]),
                HttpVersion.from(split[2]));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public String getPath() {
        return requestUri.getPath();
    }

    public String getQueryParamValue(final String key) {
        return requestUri.getQueryParams().get(key);
    }
}
