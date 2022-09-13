package org.apache.coyote.http11.header;

import java.util.Objects;

public class HttpHeader {

    private final String key;
    private final String value;

    public HttpHeader(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HttpHeader)) {
            return false;
        }
        final HttpHeader that = (HttpHeader) o;
        return Objects.equals(getKey(), that.getKey()) && Objects.equals(getValue(), that.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getValue());
    }
}
