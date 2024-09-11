package org.apache.coyote.http11.cookie;

public class Cookie {
    private String name;
    private String value;

    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }
}
