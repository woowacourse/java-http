package org.apache.coyote.http11;

import java.util.Map;

public record HttpRequest(
        String method,
        String path,
        Map<String, String> parameters
) {

    public HttpRequest {
        parameters = Map.copyOf(parameters);
    }
}
