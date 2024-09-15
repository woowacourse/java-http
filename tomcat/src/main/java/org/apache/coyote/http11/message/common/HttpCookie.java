package org.apache.coyote.http11.message.common;

import java.util.HashMap;
import java.util.StringTokenizer;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String EMPTY = "";

    private final HashMap<String, String> cookie = new HashMap<>();

    public HttpCookie(String cookies) {
        StringTokenizer tokenizer = new StringTokenizer(cookies, ";");
        while (tokenizer.hasMoreTokens()) {
            String[] line = tokenizer.nextToken().strip().split("=");
            if (line.length != 2) {
                throw new IllegalArgumentException("올바르지 않은 Cookie 형식입니다.");
            }
            cookie.put(line[0], line[1]);
        }
    }

    public boolean hasKey(String key) {
        return cookie.containsKey(key);
    }

    public boolean hasSessionId() {
        return cookie.containsKey(JSESSIONID);
    }

    public String getSessionId() {
        return cookie.getOrDefault(JSESSIONID, EMPTY);
    }
}
