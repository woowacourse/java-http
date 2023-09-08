package org.apache.coyote.http11.request;

import static org.apache.coyote.http11.request.HttpRequestUri.KEY_INDEX;
import static org.apache.coyote.http11.request.HttpRequestUri.VALUE_INDEX;

import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpCookie {

    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final String cookieHeader) {
        final Map<String, String> cookies = Pattern.compile(";")
                .splitAsStream(cookieHeader.trim())
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toUnmodifiableMap(cookie -> cookie[KEY_INDEX], cookie -> cookie[VALUE_INDEX]));
        return new HttpCookie(cookies);
    }

    public boolean containsKey(final String key) {
        return cookies.containsKey(key);
    }

    public String getValue(final String key) {
        if (!cookies.containsKey(key)) {
            return null;
        }
        return cookies.get(key);
    }
}
