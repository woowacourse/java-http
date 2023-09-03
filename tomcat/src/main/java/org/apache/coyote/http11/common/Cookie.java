package org.apache.coyote.http11.common;

public class Cookie {

    private final String name;
    private final String value;

    public Cookie(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    public static Cookie from(String cookie) {
        String[] parts = cookie.split("=");
        System.out.println("parts[0] = " + parts[0]);
        return new Cookie(parts[0], parts[1]);

    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
