package org.apache.coyote.http11.request.header;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.isNull;

public class Cookies {

    private static final String JAVA_SESSION_ID = "JSESSIONID";
    private static final String DELIMITER = "; ";
    private static final int KEY_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static final String KEY_VALUE_DELIMITER = "=";

    private final Map<String, String> entries;

    public Cookies(final Map<String, String> entries) {
        this.entries = entries;
    }

    public static Cookies from(final String cookieValue) {
        final Map<String, String> result = new HashMap<>();

        if (isNull(cookieValue)) {
            return new Cookies(result);
        }

        final String[] cookies = cookieValue.split(DELIMITER);
        Arrays.stream(cookies)
                .map(cookie -> cookie.split(KEY_VALUE_DELIMITER))
                .forEach(cookie -> result.put(cookie[KEY_INDEX], cookie[VALUE_INDEX]));

        return new Cookies(result);
    }

    public static String createNewSession() {
        return JAVA_SESSION_ID + KEY_VALUE_DELIMITER + UUID.randomUUID();
    }

    public String getJavaSessionId() {
        return entries.get(JAVA_SESSION_ID);
    }
}
