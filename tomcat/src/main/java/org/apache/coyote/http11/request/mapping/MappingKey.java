package org.apache.coyote.http11.request.mapping;

import java.util.Objects;
import org.apache.coyote.http11.HttpMethod;

public class MappingKey {

    private final HttpMethod method;
    private final String uri;

    public MappingKey(final HttpMethod method, final String uri) {
        this.method = method;
        this.uri = uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MappingKey)) {
            return false;
        }
        final MappingKey that = (MappingKey) o;
        return getMethod() == that.getMethod() && Objects.equals(getUri(), that.getUri());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMethod(), getUri());
    }
}
