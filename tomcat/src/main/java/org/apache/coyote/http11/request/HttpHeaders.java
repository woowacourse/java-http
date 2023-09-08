package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpHeaders {

    private static final char KEY_VALUE_DELIMITER = ':';
    private static final String MULTIPLE_VALUE_DELIMITER = "[,;]";

    private final Map<String, List<String>> headers;

    private HttpHeaders(Map<String, List<String>> headers) {
        this.headers = headers;
    }

    public static HttpHeaders from(List<String> headers) {
        Map<String, List<String>> headerValueByKey = new HashMap<>();
        for (String header : headers) {
            int keyValueDelimiterIndex = header.indexOf(KEY_VALUE_DELIMITER);
            String key = header.substring(0, keyValueDelimiterIndex);
            String value = header.substring(keyValueDelimiterIndex + 1).trim();
            headerValueByKey.put(key, separateValuesAndTrim(value));
        }

        return new HttpHeaders(headerValueByKey);
    }

    private static List<String> separateValuesAndTrim(String value) {
        List<String> values = Arrays.asList(value.split(MULTIPLE_VALUE_DELIMITER));
        return values.stream()
                     .map(String::trim)
                     .collect(Collectors.toList());
    }

    public List<String> get(String key) {
        return headers.get(key);
    }
}
