package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum Connection {

    CLOSE,
    KEEP_ALIVE;

    public static Optional<Connection> find(String name) {
        return Arrays.stream(values())
                .filter(connection -> connection.matches(name))
                .findFirst();
    }

    private boolean matches(String name) {
        if (Objects.isNull(name)) {
            return false;
        }
        String match = name
                .toUpperCase()
                .replace("-", "_")
                .trim();
        return match.equals(this.name());
    }
}
