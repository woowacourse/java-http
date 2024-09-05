package org.apache.catalina.response;

public enum HttpStatus {
    OK(200, "OK"),
    FOUND(302, "Found"),
    ;

    private final int value;
    private final String reasonPhrase;
    HttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int getValue() {
        return value;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    @Override
    public String toString() {
        return this.value + " " + name();
    }
}
