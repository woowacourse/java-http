package org.apache.coyote.http11.handler;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class HandlerStatus {
    private final String httpMethod;
    private final String path;
    private final Set<String> queryParameterKeys;

    public HandlerStatus(final String httpMethod, final String path) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParameterKeys = new HashSet<>();
    }

    public HandlerStatus(final String httpMethod, final String path, final Set<String> queryParameterKeys) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.queryParameterKeys = queryParameterKeys;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Set<String> getQueryParameterKeys() {
        return queryParameterKeys;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HandlerStatus)) {
            return false;
        }
        final HandlerStatus that = (HandlerStatus) o;
        return Objects.equals(httpMethod, that.httpMethod) && Objects.equals(path, that.path)
                && Objects.equals(queryParameterKeys, that.queryParameterKeys);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, path, queryParameterKeys);
    }
}
