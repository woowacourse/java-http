package org.apache.http;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private String jSessionId;

    public void parseCookie(final String cookie) {
        String[] cookieParts = cookie.split(";");
        Map<String, String> cookieKeysAndValues = new HashMap<>();
        for (String cookiePart : cookieParts) {
            String[] cookieKeyAndValue = cookiePart.split("=");
            cookieKeysAndValues.put(cookieKeyAndValue[0], cookieKeyAndValue[1]);
        }

        this.jSessionId = cookieKeysAndValues.get("JSESSIONID");
    }

    public boolean isJSessionIdEmpty() {
        return jSessionId == null;
    }

    public String getjSessionId() {
        return jSessionId;
    }

    public void setjSessionId(String sessionId) {
        this.jSessionId = sessionId;
    }
}
