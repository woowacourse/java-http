package org.apache.coyote.http11;

import java.util.LinkedHashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies = new LinkedHashMap<String, String>();

    public HttpCookie() {}

    public HttpCookie(String cookieHeader) {
        String[] cookiePairs = cookieHeader.split(";");
        for (String cookiePair : cookiePairs) {
            String[] parts = cookiePair.trim().split("=", 2);

            if (parts.length == 2) {
                String name = parts[0].trim();
                String value = parts[1].trim();

                cookies.put(name, value);
            }
        }
    }

    public boolean hasJsessionId() {
        for (String key : cookies.keySet()) {
            if ("JSESSIONID".equals(key)) {
                return true;
            }
        }
        return false;
    }

    public String getJsessionId() {
        return cookies.get("JSESSIONID");
    }
}
