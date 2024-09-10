package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HttpCookie {
    private static final String JSESSIONID = "JSESSIONID";
    private final Map<String, String> cookies = new HashMap<>();

    public HttpCookie(String cookieHeader) {
        if (Objects.nonNull(cookieHeader) && !cookieHeader.isEmpty()) {
            parseCookies(cookieHeader);
        }
    }

    public static String ofJSessionId(String value) {
        return JSESSIONID + "=" + value;
    }

    private void parseCookies(String cookieHeader) {
        String[] cookiePairs = cookieHeader.split("; ");
        for (String cookiePair : cookiePairs) {
            String[] keyValue = cookiePair.split("=", 2);
            if (keyValue.length == 2) {
                cookies.put(keyValue[0], keyValue[1]);
            }
        }
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJsessionid() {
        return cookies.get(JSESSIONID);
    }
}
