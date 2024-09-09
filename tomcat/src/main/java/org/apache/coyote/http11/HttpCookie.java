package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private final Map<String, String> cookies;

    public HttpCookie(final String header) {
        this.cookies = parseCookieHeader(header);
    }

    private Map<String, String> parseCookieHeader(final String header) {
        Map<String, String> cookiesStore = new HashMap<>();
        for (String cookie : header.split("; ")) {
            String[] nameValue = cookie.split("=");
            cookiesStore.put(nameValue[0], nameValue[1]);
        }
        return cookiesStore;
    }

    public boolean existSessionId(String seessionName) {
        return cookies.containsKey(seessionName);
    }

    public void addSessionId(final String sessionID) {
        String[] nameValue = sessionID.split("=");
        cookies.put(nameValue[0], nameValue[1]);
    }
}
