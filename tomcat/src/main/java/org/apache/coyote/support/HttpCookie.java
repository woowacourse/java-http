package org.apache.coyote.support;

import java.util.UUID;

public class HttpCookie {

    private static final String JSESSIONID_MESSAGE = "JSESSIONID";

    private final String value;

    public HttpCookie() {
        this.value = UUID.randomUUID().toString();
    }

    public String text() {
        return String.format("%s=%s", JSESSIONID_MESSAGE, value);
    }
}
