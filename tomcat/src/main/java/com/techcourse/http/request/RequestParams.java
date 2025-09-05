package com.techcourse.http.request;

import java.util.HashMap;
import java.util.Map;

public record RequestParams(
        Map<String, String> queryParameters
) {

    public static RequestParams from(final String queryString) {
        return new RequestParams(toQueryParameters(queryString));
    }

    private static Map<String, String> toQueryParameters(final String queryString) {
        Map<String, String> queryParameters = new HashMap<>();

        if (queryString.isEmpty()) {
            return queryParameters;
        }

        String[] queryParameterPairs = queryString.split("&");
        for (String queryParameterPair : queryParameterPairs) {
            String[] keyValuePair = queryParameterPair.split("=");
            queryParameters.put(keyValuePair[0], keyValuePair[1]);
        }

        return queryParameters;
    }
}
