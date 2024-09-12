package org.apache.tomcat.http.response;

import java.util.Objects;

public class StatusCode {

    private final String name;
    private final int value;

    public StatusCode(final String name, final int value) {
        this.name = name;
        this.value = value;
    }

    public String getResponseText() {
        return value + " " + name;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final StatusCode that = (StatusCode) o;
        return value == that.value && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
