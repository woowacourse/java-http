package org.apache.coyote.http11;

import java.util.UUID;

public class Cookie {

    private final String value;

    public Cookie(final String cookie) {
        this.value = cookie;
    }

    public boolean isJSessionCookie() {
        if (value == null) {
            return false;
        }
        return value.contains("JSESSIONID=");
    }

    public String getJSessionId() {
        return value.split("=")[1];
    }

    public String getValue() {
        return value;
    }
}
