package org.apache.coyote.util.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.util.Cookie;

public record HttpRequest(
        String method,
        String path,
        String version,
        Map<String, String> queries,
        Cookie cookie
) {

    public HttpRequest {
        if (queries == null) {
            queries = new HashMap<>();
        }
        if (cookie == null) {
            cookie = Cookie.parse(null);
        }
    }

    public Optional<String> getQueryValue(String key) {
        return Optional.ofNullable(queries.get(key));
    }

    public boolean hasQueries() {
        return !queries.isEmpty();
    }

    public Cookie getCookie() {
        return cookie;
    }
}
