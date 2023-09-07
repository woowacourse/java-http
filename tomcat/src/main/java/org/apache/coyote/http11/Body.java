package org.apache.coyote.http11;

public class Body {

    public static Body EMPTY = new Body("");

    private final String message;

    public Body(String message) {
        this.message = message;
    }

    public String message() {
        return this.message;
    }
}
