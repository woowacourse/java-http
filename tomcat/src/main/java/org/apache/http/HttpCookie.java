package org.apache.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private String jSessionId;

    public void parseCookie(final String cookie) {
        String[] cookieParts = cookie.split(";");
        Map<String, String> cookieKeysAndValues = new HashMap<>();
        for (String cookiePart : cookieParts) {
            if (cookiePart.contains("=")) {
                String[] cookieKeyAndValue = cookiePart.trim().split("=");
                cookieKeysAndValues.put(cookieKeyAndValue[0].trim(), cookieKeyAndValue[1].trim());
            }
        }

        this.jSessionId = cookieKeysAndValues.get("JSESSIONID");
    }

    public boolean isJSessionIdEmpty() {
        return jSessionId == null;
    }

    public String getKeyAndJSessionID() {
        return "JSESSIONID=" + jSessionId;
    }

    public void setjSessionId(String sessionId) {
        this.jSessionId = sessionId;
    }

    public String getJSessionId() {
        return this.jSessionId;
    }
}
