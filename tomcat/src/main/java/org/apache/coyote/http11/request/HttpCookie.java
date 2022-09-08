package org.apache.coyote.http11.request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpCookie {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Map<String, String> cookies;

    private HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(String cookies) {
        log.info("cookie ::: {}", cookies);
        Map<String, String> refinedCookies = Arrays.stream(cookies.split("; "))
                .map(cookie -> cookie.split("="))
                .collect(Collectors.toMap(cookie -> cookie[0], cookie -> cookie[1]));

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
}
