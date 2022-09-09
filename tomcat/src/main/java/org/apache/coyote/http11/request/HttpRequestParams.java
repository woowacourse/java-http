package org.apache.coyote.http11.request;

import java.util.Map;
import utils.ParseUtils;

public class HttpRequestParams {
    private static final String REGEX_1 = "&";
    private static final String REGEX_2 = "=";
    private static final String PARAMS = "?";
    private final Map<String, String> params;

    private HttpRequestParams(Map<String, String> params) {
        this.params = params;
    }

    public static HttpRequestParams from(final String url) {
        if (!url.contains(PARAMS)) {
            return new HttpRequestParams(Map.of());
        }

        String params = url.split("\\?")[1];

        return new HttpRequestParams(ParseUtils.parse(params, REGEX_1, REGEX_2));
    }
}
