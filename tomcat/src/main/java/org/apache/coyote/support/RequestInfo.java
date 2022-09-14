package org.apache.coyote.support;

import org.apache.coyote.HttpMethod;

import java.util.Objects;

public class RequestInfo {

    private final HttpMethod httpMethod;
    private final String uri;
    private final String queryString;

    public RequestInfo(final HttpMethod httpMethod, final String uri) {
        this(httpMethod, uri, "");
    }

    public RequestInfo(final HttpMethod httpMethod, final String uri, final String queryString) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.queryString = queryString;
    }

    public String getUri() {
        return this.uri;
    }

    public boolean sameHttpMethod(final HttpMethod httpMethod) {
        return this.httpMethod == httpMethod;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof RequestInfo)) return false;
        final RequestInfo that = (RequestInfo) o;
        return httpMethod == that.httpMethod && Objects.equals(uri, that.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, uri);
    }
}
