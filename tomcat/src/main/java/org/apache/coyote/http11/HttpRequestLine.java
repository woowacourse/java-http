package org.apache.coyote.http11;

import java.net.URI;
import java.util.Optional;

record HttpRequestLine(String method, URI uri, String version) {

    private static final int REQUEST_LINE_PART_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int TARGET_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    static Optional<HttpRequestLine> parse(final String value) {
        final var parts = value.split(" ", -1);
        if (parts.length != REQUEST_LINE_PART_COUNT) {
            return Optional.empty();
        }

        try {
            return Optional.of(new HttpRequestLine(
                    parts[METHOD_INDEX],
                    URI.create(parts[TARGET_INDEX]),
                    parts[VERSION_INDEX]));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
