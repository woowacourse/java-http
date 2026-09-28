package org.apache.coyote.http11.request;

import java.util.Objects;

public final class ProtocolVersion {
    private final String value;

    private ProtocolVersion(String value) {
        this.value = Objects.requireNonNull(value).trim();
    }

    public static ProtocolVersion from(String value) {
        return new ProtocolVersion(value);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
