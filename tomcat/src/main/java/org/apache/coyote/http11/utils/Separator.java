package org.apache.coyote.http11.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Separator {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final int KEY_VALUE_PAIR_SIZE = 2;

    public static Map<String, String> separateKeyValueBy(List<String> targets, String delimiter) {
        Map<String, String> result = new HashMap<>();
        targets.forEach(target -> {
            String[] split = target.split(delimiter, KEY_VALUE_PAIR_SIZE);
            if (split.length != KEY_VALUE_PAIR_SIZE || split[KEY_INDEX].isBlank() || split[VALUE_INDEX].isBlank()) {
                throw new IllegalArgumentException("Only Key-Value pair can be separated.");
            }
            result.put(split[KEY_INDEX].trim(), split[VALUE_INDEX].trim());
        });
        return result;
    }
}
