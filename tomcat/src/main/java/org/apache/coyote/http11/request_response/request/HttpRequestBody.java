package org.apache.coyote.http11.request_response.request;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class HttpRequestBody {

    private final String value;

    public HttpRequestBody(String value) {
        this.value = value;
    }

    public Map<String, String> parseFormData() {
        try {
            Map<String, String> queryParameters = new HashMap<>();
            Arrays.stream(value.split("&"))
                .map(parameter -> parameter.split("="))
                .forEach(keyValue -> queryParameters.put(keyValue[0], keyValue.length == 2 ? keyValue[1] : null));
            return Collections.unmodifiableMap(queryParameters);
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("invalid form data: " + value);
        }
    }

    public String getValue() {
        return value;
    }
}
