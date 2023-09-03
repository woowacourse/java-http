package org.apache.coyote.http11.request.requestLine;

import org.apache.coyote.http11.common.HttpVersion;
import org.apache.coyote.http11.exception.IllegalRequestLineException;
import org.apache.coyote.http11.request.requestLine.requestUri.RequestUri;

import java.util.Objects;
import java.util.StringTokenizer;

public class RequestLine {

    private static int REQUEST_LINE_ELEMENT_COUNT = 3;

    private final HttpMethod httpMethod;
    private final RequestUri requestUri;
    private final HttpVersion httpVersion;

    private RequestLine(HttpMethod httpMethod, RequestUri requestUri, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
        this.httpVersion = httpVersion;
    }

    public static RequestLine from(final String requestLine) {
        final StringTokenizer stringTokenizer = new StringTokenizer(requestLine, " ");

        if (stringTokenizer.countTokens() != REQUEST_LINE_ELEMENT_COUNT) {
            throw new IllegalRequestLineException("HTTP Request Line 형식이 올바르지 않습니다.");
        }

        return new RequestLine(
                HttpMethod.from(stringTokenizer.nextToken()),
                RequestUri.from(stringTokenizer.nextToken()),
                HttpVersion.from(stringTokenizer.nextToken())
        );
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

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestLine that = (RequestLine) o;
        return httpMethod == that.httpMethod && Objects.equals(requestUri, that.requestUri) && httpVersion == that.httpVersion;
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, requestUri, httpVersion);
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
