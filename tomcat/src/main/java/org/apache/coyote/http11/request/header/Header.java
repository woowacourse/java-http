package org.apache.coyote.http11.request.header;

public enum Header {

    Accept("Accept"),
    ;

    private final String name;

    Header(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
