package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    public static final String DATA_SEPARATOR = "; ";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    private static String NAME = "Cookie";

    private Map<String, String> values;

    private Cookie(final Map<String, String> values) {
        this.values = values;
    }

    public static Cookie from(final String string) {
        final Map<String, String> values = new HashMap<>();
        if (string == null) {
            return new Cookie(values);
        }
        final String[] split = string.split(DATA_SEPARATOR);
        for (String each : split) {
            final String[] keyAndValue = each.split("=");
            values.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return new Cookie(values);
    }

    public static String getNAME() {
        return NAME;
    }

    public String getAttribute(final String name) {
        return values.get(name);
    }
}
