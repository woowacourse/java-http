package com.techcourse.model;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.regex.Pattern;

public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String value;

    public Email(final String value) {
        validateIsNull(value);
        validatePattern(value);
        this.value = value;
    }

    private static void validateIsNull(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("Email value must not be null");
        }
    }

    private void validatePattern(final String value) {
        String decodedEmail = URLDecoder.decode(value, StandardCharsets.UTF_8);
        if (!EMAIL_PATTERN.matcher(decodedEmail).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Email email)) {
            return false;
        }
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return "email= " + value;
    }
}
