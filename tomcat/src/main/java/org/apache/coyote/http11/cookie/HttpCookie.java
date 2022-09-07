package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final String SESSION_PREFIX = "JSESSIONID";
    private static final String DELIMITER = "=";

    private final Map<String, String> values = new HashMap<>();

    public HttpCookie() {
    }

    public HttpCookie(String unparsedCookies) {
        parseCookies(unparsedCookies);
    }

    public static HttpCookie createNewCookie() {
        HttpCookie httpCookie = new HttpCookie();
        httpCookie.createSessionID();
        return httpCookie;
    }

    private void parseCookies(String unparsedCookies) {
        String[] cookies = unparsedCookies.split(";");
        for (String cookie : cookies) {
            String trimmedCookie = cookie.strip();
            values.put(trimmedCookie.split(DELIMITER)[0], trimmedCookie.split(DELIMITER)[1]);
        }

        if (!values.containsKey(SESSION_PREFIX)) {
            createSessionID();
        }
    }

    public void createSessionID() {
        values.put(SESSION_PREFIX, createRandomSessionID());
    }

    public String createRandomSessionID() {
        return UUID.randomUUID().toString();
    }

    public String getAllCookies() {
        String result = "";
        for (String key : values.keySet()) {
            result += key + "=" + values.get(key) + "; ";
        }
        return result;
    }

    public boolean hasSessionCookie() {
        return values.containsKey(SESSION_PREFIX);
    }
}
