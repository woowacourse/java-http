package com.techcourse.http.request;

import com.techcourse.exception.UncheckedServletException;
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
            validateKeyValuePair(keyValuePair);
            queryParameters.put(keyValuePair[0], keyValuePair[1]);
        }

        return queryParameters;
    }

    private static void validateKeyValuePair(String[] keyValuePair) {
        if (keyValuePair.length != 2) {
            throw new UncheckedServletException("쿼리 파라미터의 형식은 'key=value' 이여야 합니다.");
        }
    }
}
