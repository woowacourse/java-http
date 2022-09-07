package org.apache.coyote.http11.context;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpCookie {

    private static final String DELIMITER = "; ";
    private static final Pattern PATTERN = Pattern.compile("(?<key>.+)=(?<value>.+)");
    private static final String SESSION_ID = "JSESSIONID";

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

    public Map<String, String> getCookies() {
        return cookies;
    }

    public String getAsString() {
        String setCookie = cookies.get(SESSION_ID);
        if (setCookie == null) {
            return "";
        }
        return "Set-Cookie: JSESSIONID=" + setCookie;
    }
}
