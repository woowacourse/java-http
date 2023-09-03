package org.apache.coyote.httpresponse;

public enum HttpStatus {
    OK(200, "OK");

    private static final String DELIMITER = " ";
    private final int value;
    private final String reasonPhrase;

    HttpStatus(int value, String reasonPhrase) {
        this.value = value;
        this.reasonPhrase = reasonPhrase;
    }

    public String getHttpStatus() {
        return value + DELIMITER + reasonPhrase;
    }
}
