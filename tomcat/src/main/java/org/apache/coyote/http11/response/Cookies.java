package org.apache.coyote.http11.response;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Objects.nonNull;

public class Cookies {

    private static final String JAVA_SESSION_ID = "JSESSIONID";
    private static final String COOKIE_HEADER = "Cookie: ";
    private static final String DELIMITER = "; ";

    private final Map<String, String> entries;

    public Cookies(final Map<String, String> entries) {
        this.entries = entries;
    }

    public static Cookies from(final String cookieHeader) {
        final Map<String, String> cookies = parse(cookieHeader);

        return new Cookies(cookies);
    }

    private static Map<String, String> parse(final String cookieHeader) {
        final Map<String, String> parsed = new HashMap<>();

        if (nonNull(cookieHeader)) {
            final String[] cookies = cookieHeader.replace(COOKIE_HEADER, "").split(DELIMITER);
            Arrays.stream(cookies)
                    .map(cookie -> cookie.split("="))
                    .forEach(cookie -> parsed.put(cookie[0], cookie[1]));
        }

        return parsed;
    }

    public boolean notExist() {
        return !entries.containsKey(JAVA_SESSION_ID);
    }

    public String createNewCookieHeader() {
        return "Set-Cookie: " + JAVA_SESSION_ID + "=" + UUID.randomUUID();
    }

    public String getCookieValue() {
        return entries.get(JAVA_SESSION_ID);
    }
}
