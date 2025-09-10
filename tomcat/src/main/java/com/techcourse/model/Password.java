package com.techcourse.model;

import java.util.Objects;

public class Password {

    private final String value;

    public Password(final String value) {
        validateIsNull(value);
        this.value = value;
    }

    private void validateIsNull(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Password value must not be null");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Password password)) {
            return false;
        }
        return Objects.equals(value, password.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
