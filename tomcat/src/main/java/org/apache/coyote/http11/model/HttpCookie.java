package org.apache.coyote.http11.model;

import java.util.Map;

public class HttpCookie {

    private static final String JSESSIONID = "JSESSIONID";

    private final Map<String, String> cookies;

    public HttpCookie(final Map<String, String> cookies) {
        this.cookies = cookies;
    }

    public static HttpCookie from(final Parameters parameters) {
        return new HttpCookie(parameters.getParameters());
    }

    public String getJSessionId() {
        return cookies.get(JSESSIONID);
    }
}
