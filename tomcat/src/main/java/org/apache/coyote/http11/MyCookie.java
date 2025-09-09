package org.apache.coyote.http11;

public class MyCookie {

    private final String name;
    private final String value;

    public MyCookie(final String name, final String value) {
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
