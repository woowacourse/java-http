package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class HttpCookie {

    private static final int NAME_INDEX = 0;
    private static final int VALUE_INDEX = 1;
    private static HttpCookie emptyCookieInstance;

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie of(final String rawCookie) {
        final Map<String, String> parsedCookies = new HashMap<>();

        if (!rawCookie.isEmpty()) {
            parseRawCookie(rawCookie, parsedCookies);
        }

        return new HttpCookie(parsedCookies);
    }

    public static HttpCookie createEmptyCookie() {
        return Objects.requireNonNullElseGet(emptyCookieInstance,
                () -> emptyCookieInstance = HttpCookie.of(""));
    }

    private static void parseRawCookie(final String rawCookie, final Map<String, String> parsedCookies) {
        final String[] cookies = rawCookie.split("; ");
        for (final String cookie : cookies) {
            final String[] parsedCookie = cookie.split("=");
            parsedCookies.put(parsedCookie[NAME_INDEX], parsedCookie[VALUE_INDEX]);
        }
    }

    public boolean contains(final String cookieName) {
        return cookies.containsKey(cookieName);
    }

    public List<String> getCookieNames() {
        return new ArrayList<>(cookies.keySet());
    }

    public String getCookie(final String cookieName) {
        return cookies.get(cookieName);
    }

    public void addCookie(final String cookieName, final String value) {
        cookies.put(cookieName, value);
    }

    @Override
    public String toString() {
        if (cookies.isEmpty()) {
            return "";
        }

        final StringBuilder stringBuilder = new StringBuilder("Set-Cookie: ");
        for (final String name : cookies.keySet()) {
            stringBuilder.append(name).append("=")
                    .append(cookies.get(name)).append("; ");
        }

        return StringUtils.removeEnd(stringBuilder.toString(), "; ") + " \r\n";
    }
}
