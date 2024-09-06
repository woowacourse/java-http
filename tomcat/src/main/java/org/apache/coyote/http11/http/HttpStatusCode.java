package org.apache.coyote.http11.http;

public enum HttpStatusCode {

    OK(200, "OK");

    private final int value;
    private final String reasonPhrase;

    HttpStatusCode(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public int value() {
        return value;
    }

    @Override
    public String toString() {
        return "%s %s".formatted(value, reasonPhrase);
    }
}
