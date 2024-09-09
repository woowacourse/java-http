package org.apache.coyote.http11.response;

public enum Http11StatusCode {
    OK(200), FOUND(302);

    private final int intValue;

    Http11StatusCode(int intValue) {
        this.intValue = intValue;
    }

    @Override
    public String toString() {
        return "%d %s".formatted(intValue, name());
    }
}
