package org.apache.coyote;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;

public class Headers {

    private static final String DELIMITER = ": ";

    private final Map<String, String> values;

    public Headers(final Map<String, String> values) {
        this.values = values;
    }

    public static Headers from(final List<String> httpRequestHeaders) {
        Map<String, String> headers = httpRequestHeaders.stream()
                .filter(header -> !header.isBlank())
                .collect(toMap(header -> header.split(DELIMITER)[0], header -> header.split(DELIMITER)[1]));
        return new Headers(headers);
    }

    public Map<String, String> getValues() {
        return values;
    }
}
