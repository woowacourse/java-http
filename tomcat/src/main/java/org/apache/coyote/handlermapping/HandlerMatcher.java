package org.apache.coyote.handlermapping;

import org.apache.coyote.http11.HttpMethod;

import java.util.Objects;

public class HandlerMatcher {

    private final HttpMethod httpMethod;
    private final String requestTarget;

    public HandlerMatcher(HttpMethod httpMethod, String requestTarget) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HandlerMatcher that = (HandlerMatcher) o;
        return getHttpMethod() == that.getHttpMethod() && Objects.equals(getRequestTarget(), that.getRequestTarget());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getHttpMethod(), getRequestTarget());
    }
}
