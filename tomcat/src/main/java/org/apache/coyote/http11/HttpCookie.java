package org.apache.coyote.http11;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
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
            if (keyValue.length == 2 && isNotEmpty(keyValue)) {
                String key = URLDecoder.decode(keyValue[0].trim(), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1].trim(), StandardCharsets.UTF_8);
                cookies.put(key, value);
            }
        }
    }

    private boolean isNotEmpty(String[] keyValue) {
        return Objects.nonNull(keyValue[0]) && Objects.nonNull(keyValue[1])
                && !keyValue[0].isEmpty() && !keyValue[1].isEmpty();
    }

    public boolean hasJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJsessionid() {
        return cookies.get(JSESSIONID);
    }
}
