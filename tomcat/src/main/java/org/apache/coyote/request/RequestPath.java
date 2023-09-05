package org.apache.coyote.request;

import java.util.Objects;

public class RequestPath {

    private final String source;

    public RequestPath(final String source) {
        if (Objects.isNull(source)) {
            throw new IllegalArgumentException("RequestPath 에 null 이 들어올 수 없습니다.");
        }
        this.source = source;
    }

    public String source() {
        return source;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestPath that = (RequestPath) o;
        return Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source);
    }

    @Override
    public String toString() {
        return source;
    }
}
