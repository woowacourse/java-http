package org.apache.coyote.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record HttpRequest(
        String method,
        String path,
        String version,
        Map<String, String> queries
) {

    public HttpRequest {
        if (queries == null) {
            queries = new HashMap<>();
        }
    }

    public Optional<String> getQueryValue(String key) {
        return Optional.ofNullable(queries.get(key));
    }
}
