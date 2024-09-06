package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;

public class HttpHeaders {

    private static final String HEADER_SEPARATOR = ": ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpHeaders(Map<String, String> values) {
        this.values = values;
    }

    public static HttpHeaders of(List<String> headers) {
        Map<String, String> map = new HashMap<>();
        for (String header : headers) {
            if (!isValidHeader(header)) {
                continue;
            }
            String[] splitHeader = header.split(HEADER_SEPARATOR);
            map.put(splitHeader[KEY_INDEX], splitHeader[VALUE_INDEX]);
        }
        return new HttpHeaders(map);
    }

    private static boolean isValidHeader(String header) {
        List<String> splitHeader = Arrays.stream(header.split(HEADER_SEPARATOR)).toList();
        if (splitHeader.size() != 2) {
            return false;
        }
        return splitHeader.stream()
                .noneMatch(String::isBlank);
    }

    public String get(String key) {
        return values.get(key);
    }

    public OptionalInt getAsInt(String key) {
        String value = get(key);
        if (value == null) {
            return OptionalInt.empty();
        }
        try {
            return OptionalInt.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            return OptionalInt.empty();
        }
    }
}
