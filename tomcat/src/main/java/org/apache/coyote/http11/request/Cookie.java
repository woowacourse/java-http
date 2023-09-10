package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    public static final Cookie EMPTY_COOKIE = new Cookie("", "");

    private final String key;

    private final String value;


    public Cookie(final String key,
                  final String value) {
        this.key = key;
        this.value = value;
    }

    public static Map<String, Cookie> getCookies(final String cookieHeaderValue) {
        final Map<String, Cookie> httpCookie = new HashMap<>();

        if (cookieHeaderValue == null || cookieHeaderValue.equals("")) {
            return httpCookie;
        }

        final String[] cookieEntries = cookieHeaderValue.split(";");
        for (String cookieEntry : cookieEntries) {
            final String[] cookieNameAndValue = cookieEntry.trim().split("=");
            final String cookieKey = cookieNameAndValue[0].toLowerCase();
            final String cookieValue = cookieNameAndValue[1].toLowerCase();
            final Cookie cookie = new Cookie(cookieKey, cookieValue);
            httpCookie.put(cookieKey, cookie);
        }
        return httpCookie;

    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Cookie{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}
