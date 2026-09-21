package org.apache.coyote.http11;

import java.util.Optional;

record HttpHeader(String name, String value) {

    private static final int HEADER_PART_COUNT = 2;

    static Optional<HttpHeader> parse(final String line) {
        final var parts = line.split(":", HEADER_PART_COUNT);
        if (parts.length != HEADER_PART_COUNT) {
            return Optional.empty();
        }

        final var name = parts[0].strip();
        if (name.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new HttpHeader(name, parts[1].strip()));
    }
}
