package org.apache.coyote.cookie;

import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Cookies {

    public static final String DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private Cookies(Map<String, String> values) {
        this.values = values;
    }

    public static Cookies from(List<String> values) {
        return new Cookies(values.stream()
                .map(value -> value.split(DELIMITER))
                .collect(toMap(value -> value[KEY_INDEX], value -> value[VALUE_INDEX])));
    }

    public Optional<String> getValue(String key) {
        if (values.containsKey(key)) {
            return Optional.of(values.get(key));
        }
        return Optional.empty();
    }
}
