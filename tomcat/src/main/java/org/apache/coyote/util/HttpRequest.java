package org.apache.coyote.util;

import java.util.Map;
import java.util.Optional;

public record HttpRequest(
        String method,
        String path,
        String version,
        Map<String, String> queries
) {

    public Optional<String> getQueryValue(String key) {
        try {
            return Optional.of(queries.get(key));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
