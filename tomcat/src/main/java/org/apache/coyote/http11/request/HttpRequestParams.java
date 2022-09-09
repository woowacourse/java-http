package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestParams {
    private final Map<String, String> params;

    private HttpRequestParams(Map<String, String> params) {
        this.params = params;
    }

    public static HttpRequestParams from(final String url) {
        if (!url.contains("?")) {
            return new HttpRequestParams(Map.of());
        }

        String params = url.split("\\?")[1];

        return new HttpRequestParams(Arrays.stream(params.split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1])));
    }
}
