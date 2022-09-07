package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String COOKIE_EQUAL = "=";
    private final Map<String, String> values;

    public HttpCookie(final String cookie) {
        this.values = parseCookie(cookie);
    }

    public static HttpCookie ofJSessionId(final String sessionId) {
        return new HttpCookie(JSESSIONID + COOKIE_EQUAL + sessionId);
    }

    private Map<String, String> parseCookie(final String cookie) {
        final Map<String, String> values = new LinkedHashMap<>();
        final String[] cookieValues = cookie.split(COOKIE_DELIMITER);
        for (String value : cookieValues) {
            final String[] parameterAndValue = value.split(COOKIE_EQUAL);
            values.put(parameterAndValue[0], parameterAndValue[1]);
        }
        return values;
    }

    public String getCookieValue(final String parameter) {
        if (values.isEmpty() || !values.containsKey(parameter)) {
            return "";
        }
        return values.get(parameter);
    }

    public String encodingToHttpHeader() {
        final List<String> cookieValues = new ArrayList<>();
        for (Entry<String, String> entry : this.values.entrySet()) {
            final String join = String.join(COOKIE_EQUAL, entry.getKey(), entry.getValue());
            cookieValues.add(join);
        }
        return String.join(COOKIE_DELIMITER, cookieValues);
    }
}
