package org.apache.coyote.http11.request;

import java.util.Map;

public record ParsedTarget(
        String path,
        Map<String, String> queryParameters
) {

    public boolean matchesPath(String path) {
        return this.path.equals(path);
    }
}
