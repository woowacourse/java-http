package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import java.util.UUID;

public class HttpCookie {

    private static final String JSESSIONID_KEY = "JSESSIONID";
    private static final String COOKIE_ELEMENT_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> cookie = new HashMap<>();

    public void setJsessionId() {
        cookie.put(JSESSIONID_KEY, UUID.randomUUID().toString());
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(COOKIE_ELEMENT_DELIMITER);
        for (Entry<String, String> entry : cookie.entrySet()) {
            stringJoiner.add(entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue());
        }
        return stringJoiner.toString();
    }
}
