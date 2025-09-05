package org.apache.coyote.http11.dto;

import java.util.Map;

public record HttpRequest(
        String method,
        String path,
        String version,
        Map<String, String> headers,
        Map<String, String> queryParams
) {

    public String getParam(String key) {
        return queryParams.get(key);
    }
}
