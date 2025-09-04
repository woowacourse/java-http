package org.apache.coyote;

public enum HttpStatus {

    OK(200),
    NOT_FOUND(404)
    ;

    private final int value;

    HttpStatus(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
