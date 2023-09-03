package org.apache.coyote.http11.response;

public enum HttpStatusCode {
    OK(200, "OK"),
    FOUND(302, "Found"),
    ;

    private final int code;
    private final String text;

    HttpStatusCode(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public static String message(HttpStatusCode code) {
        return String.join(" ", String.valueOf(code.code), code.text, "");
    }
}
