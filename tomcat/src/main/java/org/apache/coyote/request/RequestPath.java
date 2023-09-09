package org.apache.coyote.request;

import java.util.Objects;

public class RequestPath {

    private final String value;

    public RequestPath(final String value) {
        if (Objects.isNull(value)) {
            throw new IllegalArgumentException("RequestPath 에 null 이 들어올 수 없습니다.");
        }

        this.value = value;
    }

    public static RequestPath from(String requestPathValue) {
        return new RequestPath(requestPathValue);
    }

    public String value() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final RequestPath that = (RequestPath) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
