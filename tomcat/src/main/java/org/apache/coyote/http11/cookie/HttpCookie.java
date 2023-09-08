package org.apache.coyote.http11.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCookie {
    private static final String COOKIE_SEPARATOR = "; ";
    private static final String KEY_VALUE_SEPARATOR = "=";
    public static final String JSESSIONID_KEY = "JSESSIONID";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    private HttpCookie() {
        this(new HashMap<>());
    }

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(String cookies) {
        if (cookies.isBlank()) {
            return new HttpCookie();
        }
        Map<String, String> values = new HashMap<>();
        for (String cookie : cookies.split(COOKIE_SEPARATOR)) {
            String[] keyAndValue = cookie.split(KEY_VALUE_SEPARATOR);
            values.put(keyAndValue[KEY_INDEX], keyAndValue[VALUE_INDEX]);
        }
        return new HttpCookie(values);
    }

    public static HttpCookie jSessionId(String id) {
        Map<String, String> values = new HashMap<>();
        values.put(JSESSIONID_KEY, id);
        return new HttpCookie(values);
    }

    public List<String> names() {
        return new ArrayList<>(values.keySet());
    }

    public String getValue(String key) {
        return values.getOrDefault(key, "");
    }

}
