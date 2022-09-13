package org.apache.coyote.http11.header;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_VALUE_DELIMITER = "=";
    private static final int COOKIE_NAME_INDEX = 0;
    private static final int COOKIE_VALUE_INDEX = 1;

    private final Map<String, String> values;

    private HttpCookie() {
        this(new HashMap<>());
    }

    private HttpCookie(final Map<String, String> values) {
        this.values = values;
    }

    public static HttpCookie from(final String cookies) {
        if (cookies == null || cookies.isBlank()) {
            return new HttpCookie();
        }
        final Map<String, String> values = new HashMap<>();
        for (String cookieNameAndValue : cookies.split(COOKIE_DELIMITER)) {
            final String[] cookie = cookieNameAndValue.split(COOKIE_VALUE_DELIMITER);
            values.put(cookie[COOKIE_NAME_INDEX], cookie[COOKIE_VALUE_INDEX]);
        }
        return new HttpCookie(values);
    }

    public boolean isExist() {
        return !values.isEmpty();
    }

    public String getSessionId() {
        return values.get("JSESSIONID");
    }
}
