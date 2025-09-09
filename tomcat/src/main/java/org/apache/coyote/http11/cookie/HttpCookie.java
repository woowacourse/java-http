package org.apache.coyote.http11.cookie;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String EQUAL = "=";
    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";

    public Map<String, String> addCookie(final Map<String, String> requestHeaders) {
        Map<String, String> responseHeaders = new HashMap<>();

        String uuid = UUID.randomUUID().toString();
        String totalSetCookie = JSESSIONID + EQUAL + uuid;

        if (!requestHeaders.containsKey(COOKIE)) {
            responseHeaders.put(SET_COOKIE, totalSetCookie);
            return responseHeaders;
        }

        return responseHeaders;
    }
}
