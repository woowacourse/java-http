package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;
import org.apache.catalina.session.Session;

public class HttpCookie {

    private static final String JSESSIONID_KEY = "JSESSIONID";
    private static final String COOKIE_ELEMENT_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> values;

    public HttpCookie() {
        values = new HashMap<>();
    }

    private HttpCookie(Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie of(String cookie) {
        Map<String, String> values = new HashMap<>();
        String[] cookieElements = cookie.split(COOKIE_ELEMENT_DELIMITER);
        for (String element : cookieElements) {
            String[] keyValue = element.split(KEY_VALUE_DELIMITER);
            values.put(keyValue[KEY_INDEX], keyValue[VALUE_INDEX]);
        }
        return new HttpCookie(values);
    }

    public void setJsessionId(Session session) {
        values.put(JSESSIONID_KEY, session.getId());
    }

    public boolean hasJessionId() {
        return values.containsKey(JSESSIONID_KEY);
    }

    public String getJsessionId() {
        return values.get(JSESSIONID_KEY);
    }

    public String toString() {
        StringJoiner stringJoiner = new StringJoiner(COOKIE_ELEMENT_DELIMITER);
        for (Entry<String, String> entry : values.entrySet()) {
            stringJoiner.add(entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue());
        }
        return stringJoiner.toString();
    }
}
