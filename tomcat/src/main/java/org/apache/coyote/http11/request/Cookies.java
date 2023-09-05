package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

public class Cookies {

    private static final String JAVA_SESSION_ID = "JSESSIONID";
    private static final String COOKIE_HEADER = "Cookie: ";
    private static final String DELIMITER = "; ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;

    private final Map<String, String> entries;

    public Cookies(final Map<String, String> entries) {
        this.entries = entries;
    }

    public static Cookies from(final String cookieHeader) {
        if (isNull(cookieHeader)) {
            return new Cookies(new HashMap<>());
        }

        final Map<String, String> result = new HashMap<>();

        final String[] cookies = cookieHeader.replace(COOKIE_HEADER, "").split(DELIMITER);
        Arrays.stream(cookies)
                .map(cookie -> cookie.split("="))
                .forEach(cookie -> result.put(cookie[KEY_INDEX], cookie[VALUE_INDEX]));

        return new Cookies(result);
    }

    public boolean notExist(final String key) {
        return !entries.containsKey(key);
    }

    public String createNewJSessionIdHeader() {
        return "Set-Cookie: " + JAVA_SESSION_ID + "=" + UUID.randomUUID();
    }

    public String getValue(final String key) {
        return entries.get(key);
    }
}
