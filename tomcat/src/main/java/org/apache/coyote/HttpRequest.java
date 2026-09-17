package org.apache.coyote;

import java.util.Map;

public record HttpRequest(
        String method,
        String path,
        Map<String, String> parameters,
        HttpCookie cookies
) {

    public HttpRequest {
        parameters = Map.copyOf(parameters);
    }
}
