package com.mapping;

import java.util.Objects;
import org.apache.coyote.http11.HttpMethod;

public class RequestKey {
    private final String uri;
    private final HttpMethod method;

    public RequestKey(final String uri, final HttpMethod method) {
        this.uri = uri;
        this.method = method;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof RequestKey that)) {
            return false;
        }
        return Objects.equals(uri, that.uri) && method == that.method;
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri, method);
    }
}
