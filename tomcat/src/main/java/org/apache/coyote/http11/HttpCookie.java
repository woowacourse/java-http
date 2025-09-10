package org.apache.coyote.http11;

import java.util.UUID;

public class HttpCookie {

    private final String name;
    private final String value;

    private HttpCookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static HttpCookie of(String name, String value) {
        return new HttpCookie(name, value);
    }

    public static HttpCookie createSessionId() {
        return new HttpCookie("JSESSIONID", UUID.randomUUID().toString());
    }

    @Override
    public String toString() {
        return String.format("Set-Cookie: %s=%s", name, value);
    }
}
