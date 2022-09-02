package org.apache.coyote.http11;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.Map;

public class Http11QueryParams {
    private final Map<String, String> params;

    public Http11QueryParams(final Map<String,String> params) {
        this.params = params;
    }

    public static Http11QueryParams of(final String urlQueryParams) {
        final Map<String, String> params = Arrays.stream(urlQueryParams.split("&"))
                .map(keyValue -> keyValue.split("="))
                .collect(toMap(keyValue -> keyValue[0], keyValue -> keyValue[1]));
        return new Http11QueryParams(params);
    }

    public Map<String, String> getParams() {
        return params;
    }

    public String get(final String key) {
        return params.getOrDefault(key, null);
    }
}
