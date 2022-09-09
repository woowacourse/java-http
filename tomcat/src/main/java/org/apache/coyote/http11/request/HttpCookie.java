package org.apache.coyote.http11.request;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ParseUtils;

public class HttpCookie {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookies) {
        Map<String, String> refinedCookies = ParseUtils.parse(cookies, "; ", "=");

        return new HttpCookie(refinedCookies);
    }

    public static HttpCookie empty() {
        return new HttpCookie(new HashMap<>());
    }

    public static String ofJSessionId(final String id) {
        return "JSESSIONID=" + id;
    }

    public boolean isJSessionId() {
        return cookies.containsKey("JSESSIONID");
    }

    public String getJSessionId() {
        return cookies.get("JSESSIONID");
    }
}
