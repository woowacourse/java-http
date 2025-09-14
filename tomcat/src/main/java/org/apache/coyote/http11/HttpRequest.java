package org.apache.coyote.http11;

import java.util.Map;

public record HttpRequest(
    String method,
    String path,
    String version,
    Map<String, String> headers,
    String body,
    Map<String, String> queryParams
) {
    
    public String getQueryParam(String name) {
        return queryParams.get(name);
    }
}
