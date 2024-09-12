package org.apache.catalina;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class Cookie {

    private static final String COOKIE_DELIMITER = "&";
    private static final String SPLIT_DELIMITER = "=";

    private final Map<String, String> values;

    public Cookie(Map<String, String> values) {
        this.values = values;
    }

    public static Cookie parse(String cookie) {
        if (cookie == null) {
            return new Cookie(Map.of());
        }

        Map<String, String> values = new HashMap<>();
        String[] splits = cookie.split(COOKIE_DELIMITER);
        for (String split : splits) {
            String[] keyValue = split.split(SPLIT_DELIMITER);
            values.put(keyValue[0], keyValue[1]);
        }
        return new Cookie(values);
    }

    public String getValue(String key) {
        return values.getOrDefault(key, null);
    }

    @Override
    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(",");
        for (String key : values.keySet()) {
            stringJoiner.add(key + "=" + values.get(key));
        }
        return stringJoiner.toString();
    }
}
