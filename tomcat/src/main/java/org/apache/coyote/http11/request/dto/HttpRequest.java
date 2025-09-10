package org.apache.coyote.http11.request.dto;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.http.HttpHeaders;

public record HttpRequest(
        String method,
        String path,
        String version,
        HttpHeaders headers,
        Map<String, String> queryParams,
        Map<String, String> bodyParams,
        Map<String, String> params // merged (body 우선)
) {
    public HttpRequest {
        queryParams = (queryParams == null) ? Map.of() : Map.copyOf(queryParams);
        bodyParams  = (bodyParams  == null) ? Map.of() : Map.copyOf(bodyParams);

        Map<String, String> merged = new HashMap<>(queryParams);
        merged.putAll(bodyParams);

        params = Collections.unmodifiableMap(merged);
    }

    public String getParam(String key) {
        return params.get(key);
    }
}
