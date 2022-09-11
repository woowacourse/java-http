package org.apache.coyote.http11.context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpCookie {

    public static final String SESSION_NAME = "JSESSIONID";
    private static final String DELIMITER = "; ";
    private static final Pattern PATTERN = Pattern.compile("(?<key>.+)=(?<value>.+)");

    private final Map<String, String> cookies;

    private HttpCookie(Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public HttpCookie() {
        this(new HashMap<>());
    }

    public static HttpCookie parse(String cookieString) {
        if (cookieString == null || cookieString.isBlank()) {
            return new HttpCookie();
        }
        return new HttpCookie(parseCookieString(cookieString));
    }

    private static Map<String, String> parseCookieString(String cookieString) {
        return Arrays.stream(cookieString.split(DELIMITER))
                .map(String::trim)
                .map(PATTERN::matcher)
                .filter(Matcher::find)
                .collect(Collectors.toMap(
                        matcher -> matcher.group("key"),
                        matcher -> matcher.group("value")
                ));
    }

    public HttpCookie asResponse(String uuid) {
        if (cookies.get(SESSION_NAME) == null) {
            return new HttpCookie(Map.of(SESSION_NAME, uuid));
        }
        return new HttpCookie();
    }

    public String find(String key) {
        return cookies.get(key);
    }

    public String getAsString() {
        String setCookie = cookies.get(SESSION_NAME);
        if (setCookie == null) {
            return "";
        }
        return "Set-Cookie: JSESSIONID=" + setCookie;
    }
}
