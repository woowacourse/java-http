package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class CookieResponseHeader {

    private static final String RESPONSE_HEADER_KEY = "Set-Cookie: ";
    private static final String JAVA_SESSION_ID = "JSESSIONID";
    private static final String DELIMITER = "; ";
    private static final String KEY_VALUE_SPLITTER = "=";

    private final Map<String, String> data;

    private CookieResponseHeader(final Map<String, String> data) {
        this.data = data;
    }

    public static CookieResponseHeader createByJSessionId(final String jSessionId) {
        final Map<String, String> data = new HashMap<>();
        data.put(JAVA_SESSION_ID, jSessionId);
        return new CookieResponseHeader(data);
    }

    public static CookieResponseHeader blank() {
        return new CookieResponseHeader(new HashMap<>());
    }

    public boolean isExist() {
        return !data.isEmpty();
    }

    public String getFormattedValue() {
        return RESPONSE_HEADER_KEY + data.entrySet().stream()
                .map(entry -> entry.getKey() + KEY_VALUE_SPLITTER + entry.getValue())
                .collect(Collectors.joining(DELIMITER));
    }
}
