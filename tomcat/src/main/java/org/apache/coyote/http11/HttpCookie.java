package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCookie {

    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = new HashMap<>(values);
    }

    public static HttpCookie from(String cookie) {
        Map<String, String> values = new HashMap<>();
        List<String> cookiePairs = List.of(cookie.split("; "));

        for (String cookiePair : cookiePairs) {
            insertValue(values, cookiePair);
        }

        return new HttpCookie(values);
    }

    private static void insertValue(Map<String, String> values, String cookiePair) {
        List<String> pair = List.of(cookiePair.split("="));

        if (pair.size() == 1) {
            values.put(pair.get(KEY_INDEX), "");
            return;
        }
        values.put(pair.get(KEY_INDEX), pair.get(VALUE_INDEX));
    }

    public Map<String, String> getValues() {
        return values;
    }
}
