package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String FINISH_LINE_DELIMITER = ";";
    public static final String KEY_VALUE_DELIMITER = "=";
    private final Map<String, String> values;

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(String cookies) {
        Map<String, String> values = new HashMap<>();
        if (cookies.equals("")) {
            return new HttpCookie(values);
        }
        for (String cookie : cookies.split(FINISH_LINE_DELIMITER)) {
            String key = cookie.split(KEY_VALUE_DELIMITER)[0];
            String value = cookie.split(KEY_VALUE_DELIMITER)[1];
            values.put(key, value);
        }
        return new HttpCookie(values);
    }

    public void addCookie(String key, String value) {
        values.put(key, value);
    }

    public String getValue(String key) {
        return values.getOrDefault(key, null);
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public String getCookies() {
        return values.keySet().stream()
                .map(key -> key + KEY_VALUE_DELIMITER + values.get(key))
                .collect(Collectors.joining(FINISH_LINE_DELIMITER));
    }
}
