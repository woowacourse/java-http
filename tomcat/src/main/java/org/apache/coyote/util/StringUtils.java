package org.apache.coyote.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.apache.coyote.util.Constants.KEY_INDEX;
import static org.apache.coyote.util.Constants.VALUE_INDEX;

public class StringUtils {

    private static final String MULTIPLE_CONDITION_SEPARATOR = "&";
    private static final String KEY_VALUE_SEPARATOR = "=";

    public static Map<String, String> separateKeyValue(String term) {
        return separateKeyValue(term, KEY_VALUE_SEPARATOR, MULTIPLE_CONDITION_SEPARATOR);
    }

    public static Map<String, String> separateKeyValue(String term, String keyValueSeparator, String separator) {
        List<String> bodies = List.of(term.split(separator));
        return bodies.stream()
                .map(String::trim)
                .map(s -> s.split(keyValueSeparator))
                .collect(Collectors.toMap(s -> s[KEY_INDEX], s -> s[VALUE_INDEX]));
    }

    private StringUtils() {

    }
}
