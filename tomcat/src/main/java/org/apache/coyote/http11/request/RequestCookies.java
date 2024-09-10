package org.apache.coyote.http11.request;

import static java.util.stream.Collectors.toMap;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class RequestCookies {

    public static final String KEY_DELIMITER = "=";
    public static final String COOKIE_DELIMITER = ";";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private RequestCookies(Map<String, String> values) {
        this.values = values;
    }

    public static RequestCookies from(List<String> values) {
        return new RequestCookies(values.stream()
                .flatMap(value -> Arrays.stream(value.split(COOKIE_DELIMITER)))
                .map(parts -> parts.split(KEY_DELIMITER))
                .collect(toMap(value -> value[KEY_INDEX].trim(), value -> value[VALUE_INDEX].trim())));
    }

    public Optional<String> getValue(String key) {
        if (values.containsKey(key)) {
            return Optional.of(values.get(key));
        }
        return Optional.empty();
    }

    @Override
    public String toString() {
        String keyValues = values.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(",", "(", ")"));
        return "RequestCookies{" +
               "values=" + keyValues +
               '}';
    }
}
