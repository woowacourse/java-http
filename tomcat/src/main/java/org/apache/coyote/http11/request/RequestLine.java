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

    public String getPath() {
        final String path = requestUri.getPath();
        if (path.equals("/")) {
            return path;
        }
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }
}
