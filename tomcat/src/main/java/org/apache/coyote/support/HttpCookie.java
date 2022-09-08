package org.apache.coyote.support;

import java.util.UUID;

public class HttpCookie {

    public static final String HEADER_CONSTANT = "Cookie";
    private static final String JSESSIONID_MESSAGE = "JSESSIONID";

    private final String jSessionId;

    private HttpCookie(String jSessionId) {
        this.jSessionId = jSessionId;
    }

    public static HttpCookie ofRandomUuid() {
        return new HttpCookie(UUID.randomUUID().toString());
    }

    public static HttpCookie ofJSessionId(String jSessionId) {
        return new HttpCookie(jSessionId);
    }

    public String text() {
        return String.format("%s=%s", JSESSIONID_MESSAGE, jSessionId);
    }

    public String value() {
        return jSessionId;
    }
}
