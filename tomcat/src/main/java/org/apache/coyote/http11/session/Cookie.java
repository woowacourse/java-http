package org.apache.coyote.http11.session;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.HttpHeader;

public class Cookie {

    private static final String COOKIE_HEADER = "Cookie";

    private final Map<String, String> cookies;

    private Cookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static Cookie from(final HttpHeader httpHeader) {
        if (httpHeader.hasHeader(COOKIE_HEADER)) {
            final Map<String, String> parsedCookies = parseCookie(httpHeader);
            return new Cookie(parsedCookies);
        }
        return Cookie.emptyCookie();
    }

    private static Map<String, String> parseCookie(final HttpHeader httpHeader) {
        final Map<String, String> cookies = new HashMap<>();
        final String cookieString = httpHeader.get(COOKIE_HEADER);

        final String[] splitCookies = cookieString.split("; ");
        for (final String splitCookie : splitCookies) {
            final String[] cookieKeyValue = splitCookie.split("=");
            if (cookieKeyValue.length < 2) {
                continue;
            }
            cookies.put(cookieKeyValue[0], cookieKeyValue[1]);
        }
        return cookies;
    }

    public static Cookie emptyCookie() {
        return new Cookie(new HashMap<>());
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

    public boolean hasKey(final String cookie) {
        return this.cookies.containsKey(cookie);
    }

    public String getCookie(final String cookie) {
        return cookies.get(cookie);
    }

    public void addCookie(final String key, final String value) {
        cookies.put(key, value);
    }
}
