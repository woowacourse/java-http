package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String EQUAL = "=";
    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";

    private final Map<String, String> cookies = new HashMap<>();

    public Map<String, String> parseCookie(final Map<String, String> requestHeaders, final String uuid) {
        Map<String, String> answer = new HashMap<>();
        String totalSetCookie = JSESSIONID + EQUAL + uuid;

        if (!requestHeaders.containsKey(COOKIE)) {
            answer.put(SET_COOKIE, totalSetCookie);
            cookies.put(JSESSIONID, uuid);
            return answer;
        }

        return answer;
    }
}
