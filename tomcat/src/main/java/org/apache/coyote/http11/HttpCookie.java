package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String COOKIE_HEADER_SEPARATOR = "; ";
    private static final String COOKIE_VALUE_SEPARATOR = "=";
    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeader) {
        if (cookieHeader == null) {
            return new HttpCookie(new HashMap<>());
        }
        final var cookies = new HashMap<String, String>();
        final String[] splitHeader = cookieHeader.split(COOKIE_HEADER_SEPARATOR);
        for (var value : splitHeader) {
            final String[] splitValues = value.split(COOKIE_VALUE_SEPARATOR);
            cookies.put(splitValues[0], splitValues[1]);
        }
        return new HttpCookie(cookies);
    }

    public String getJSessionId(final boolean create) {
        if (create) {
            cookies.put(JSESSIONID, UUID.randomUUID().toString());
        }
        return cookies.get(JSESSIONID);
    }
}
