package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class HttpCookies {

    private static final String JSESSIONID_KEY = "JSESSIONID";
    private static final String COOKIE_DELIMITER = "; ";
    private static final String KEY_VALUE_DELIMITER = "=";
    private static final String CRLF = "\r\n";

    private final Map<String, String> cookies;

    public HttpCookies() {
        this.cookies = new HashMap<>();
    }

    public HttpCookies(String rawCookies) {
        if (rawCookies == null) {
            this.cookies = new HashMap<>();
            return;
        }
        Map<String, String> cookies = new HashMap<>();
        for (String rawCookie : rawCookies.split(COOKIE_DELIMITER)) {
            String[] token = rawCookie.split(KEY_VALUE_DELIMITER);
            cookies.put(token[0], token[1]);
        }
        this.cookies = cookies;
    }

    public void addJSessionId(String jSessionId) {
        cookies.put(JSESSIONID_KEY, jSessionId);
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID_KEY);
    }

    public String toSetCookieValue() {
        if (cookies.isEmpty()) {
            return null;
        }

        StringBuilder setCookie = new StringBuilder();
        for (String key : cookies.keySet()) {
            setCookie.append(String.format("%s=%s", key, cookies.get(key))).append(COOKIE_DELIMITER);
        }
        setCookie.setLength(setCookie.length() - 2);
        setCookie.append(CRLF);

        return setCookie.toString();
    }
}
