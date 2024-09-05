package org.apache.coyote.http11;

import util.BiValue;
import util.StringUtil;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class Cookies {
    private static final String DELIMITER = "; ";
    private static final String COOKIE_DELIMITER = "=";

    private final Map<String, String> values;

    public static Cookies from(final String line) {
        return new Cookies(Arrays.stream(line.split(DELIMITER))
                .map(s -> StringUtil.splitBiValue(s, COOKIE_DELIMITER))
                .filter(BiValue::secondNotNull)
                .collect(Collectors.toMap(BiValue::first, BiValue::second)));
    }

    public Cookies(final Map<String, String> values) {
        this.values = values;
    }

    public String getCookie(final String name) {
        return StringUtil.blankIfNull(values.get(name));
    }

    @Override
    public String toString() {
        return "Cookies{\n" +
                values.entrySet()
                        .stream()
                        .map(entry -> "  " + entry.getKey() + ": " + entry.getValue())
                        .collect(Collectors.joining(",\n")) +
                "\n}";
    }
}
