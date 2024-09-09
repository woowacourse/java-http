package org.apache.coyote.util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StringUtils {

    public static final String MULTIPLE_CONDITION_SEPARATOR = "&";
    public static final String KEY_VALUE_SEPARATOR = "=";

    public static Map<String, String> separateKeyValue(String term) {
        List<String> bodies = List.of(term.split(MULTIPLE_CONDITION_SEPARATOR));
        return bodies.stream()
                .map(s -> s.split(KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    private StringUtils() {

    }
}
