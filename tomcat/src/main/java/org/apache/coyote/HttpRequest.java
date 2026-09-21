package org.apache.coyote;

import java.util.Map;

public record HttpRequest(
        String method,
        String path,
        String version,
        Map<String, String> headers,
        Map<String, String> queryParameters,
        Map<String, String> formParameters,
        String body,
        HttpCookie cookies
) {

    public HttpRequest {
        headers = Map.copyOf(headers);
        queryParameters = Map.copyOf(queryParameters);
        formParameters = Map.copyOf(formParameters);
    }
}
