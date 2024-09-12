package org.apache.coyote.coockie;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HttpCookie {

    private static final String JSESSIONID_VALUE = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(String rawCookies) {
        this.cookies = mapCookies(rawCookies);
    }

    private static Map<String, String> mapCookies(String rawCookies) {
        Map<String, String> cookieGroup = new HashMap<>();

        if (rawCookies != null && !rawCookies.isBlank()) {
            String[] cookiesElements = rawCookies.split("; ");
            for (int i = 0; i < cookiesElements.length; i++) {
                String[] cookiePair = cookiesElements[i].split("=");
                cookieGroup.put(cookiePair[0], cookiePair[1]);
            }
        }
        return cookieGroup;
    }

    public boolean hasSessionId() {
        return cookies.containsKey(JSESSIONID_VALUE);
    }

    public String getSessionId() {
        return cookies.get(JSESSIONID_VALUE);
    }

    public String getResponse() {
        if (cookies.isEmpty()) {
            return "";
        }

        StringBuilder response = new StringBuilder();
        for (Entry<String, String> stringStringEntry : cookies.entrySet()) {
            response.append(stringStringEntry.getKey())
                    .append("=")
                    .append(stringStringEntry.getValue())
                    .append(";");
        }
        return String.valueOf(response);
    }
}
