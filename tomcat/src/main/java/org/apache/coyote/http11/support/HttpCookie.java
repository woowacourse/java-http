package org.apache.coyote.http11.support;

import java.util.UUID;

public class HttpCookie {

    private final String key;
    private final String value;

    private HttpCookie(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static HttpCookie create() {
        return new HttpCookie("JSESSIONID", String.valueOf(UUID.randomUUID()));
    }

    public String format() {
        return key + "=" + value;
    }

    public String getValue() {
        return value;
    }
}
