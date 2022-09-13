package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import utils.ParseUtils;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String REGEX_1 = "; ";
    private static final String REGEX_2 = "=";
    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookies) {
        Map<String, String> refinedCookies = ParseUtils.parse(cookies, REGEX_1, REGEX_2);

        return new HttpCookie(refinedCookies);
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public boolean isJSessionId() {
        return cookies.containsKey(JSESSIONID);
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }
}
