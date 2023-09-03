package org.apache.coyote.handler;

import org.apache.coyote.request.HttpRequest;

import java.util.Objects;

public class MappingInfo {

    private final String httpMethod;
    private final String requestUri;

    public MappingInfo(final String httpMethod, final String requestUri) {
        this.httpMethod = httpMethod;
        this.requestUri = requestUri;
    }

    public static MappingInfo from(final HttpRequest request) {
        return new MappingInfo(request.httpMethod().name(), request.requestUri().source());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MappingInfo that = (MappingInfo) o;
        return Objects.equals(httpMethod, that.httpMethod) && Objects.equals(requestUri, that.requestUri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(httpMethod, requestUri);
    }
}
