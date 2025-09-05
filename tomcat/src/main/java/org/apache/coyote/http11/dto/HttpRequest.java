package org.apache.coyote.http11.dto;

import java.util.Map;
import org.apache.coyote.http11.util.HttpHeaders;

public record HttpRequest(
        String method,
        String path,
        String version,
        HttpHeaders headers,
        Map<String, String> queryParams
) {

    public String getParam(String key) {
        return queryParams.get(key);
    }
}
