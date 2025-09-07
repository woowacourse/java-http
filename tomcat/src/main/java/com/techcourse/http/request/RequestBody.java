package com.techcourse.http.request;

import com.techcourse.exception.UncheckedServletException;
import java.util.HashMap;
import java.util.Map;

public record RequestBody(
        Map<String, String> values
) {

    public static RequestBody from(final String requestBody) {
        return new RequestBody(convertToRequestBodyMap(requestBody));
    }

    public static RequestBody empty() {
        return new RequestBody(new HashMap<>());
    }

    private static Map<String, String> convertToRequestBodyMap(final String requestBodyString) {
        Map<String, String> requestBodyMap = new HashMap<>();

        if (requestBodyString.isEmpty()) {
            return requestBodyMap;
        }

        String[] requestBodyPairs = requestBodyString.split("&");
        for (String requestBodyPair : requestBodyPairs) {
            String[] keyValuePair = requestBodyPair.split("=");
            validateKeyValuePair(keyValuePair);
            requestBodyMap.put(keyValuePair[0], keyValuePair[1]);
        }

        return requestBodyMap;
    }

    private static void validateKeyValuePair(String[] keyValuePair) {
        if (keyValuePair.length != 2) {
            throw new UncheckedServletException("request body의 형식은 'key=value' 이여야 합니다.");
        }
    }
}
