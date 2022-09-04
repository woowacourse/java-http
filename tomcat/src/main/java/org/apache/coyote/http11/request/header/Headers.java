package org.apache.coyote.http11.request.header;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Headers {

    private final Map<String, String> values;

    private Headers(final Map<String, String> values) {
        this.values = values;
    }

    public static Headers from(final List<String> headerLines) {
        final Map<String, String> values = headerLines.stream()
                .map(line -> line.split(": ", 2))
                .collect(Collectors.toMap(
                        splitLine -> splitLine[0],
                        splitLine -> splitLine[1]
                ));
        return new Headers(values);
    }

    public Optional<String> get(final Header header) {
        return Optional.ofNullable(values.get(header.getName()));
    }
}
