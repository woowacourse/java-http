package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> values;

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String rawCookie) {
        final String[] cookieData = rawCookie.split(COOKIE_DELIMITER);
        final Map<String, String> cookieValues = new HashMap<>();
        for (final String data : cookieData) {
            final String[] keyValue = data.split(KEY_VALUE_DELIMITER);
            cookieValues.put(keyValue[0], keyValue[1]);
        }

        return new HttpCookie(cookieValues);
    }

    public static HttpCookie fromJSessionId(final String sessionId) {
        final Map<String, String> values = new HashMap<>();
        values.put(JSESSIONID, sessionId);

        return new HttpCookie(values);
    }

    public String getJSessionID() {
        if (values.containsKey(JSESSIONID)) {
            return values.get(JSESSIONID);
        }
        throw new IllegalStateException("JSESSIONID가 존재하지 않습니다.");
    }

    public Map<String, String> getValues() {
        return new HashMap<>(values);
    }

    @Override
    public String toString() {
        return String.join(COOKIE_DELIMITER, values.entrySet().stream()
            .map(entry -> entry.getKey() + KEY_VALUE_DELIMITER + entry.getValue())
            .toArray(String[]::new));
    }
}
