package com.techcourse.model;

import java.util.Objects;

public class Account {

    private final String value;

    public Account(final String value) {
        validateIsNull(value);
        this.value = value;
    }

    private void validateIsNull(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Account value must not be null");
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Account account)) {
            return false;
        }
        return Objects.equals(value, account.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "account= " + value;
    }
}
