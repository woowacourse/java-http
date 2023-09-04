package org.apache.coyote.http11;

public class HttpStatus {

    public static final HttpStatus OK = new HttpStatus(200, "OK");
    public static final HttpStatus FOUND = new HttpStatus(302, "FOUND");
    private static final String WHITE_SPACE = " ";

    private final int code;
    private final String text;

    public HttpStatus(final int code, final String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    public String getText() {
        return text;
    }

    public String toLine() {
        return code + WHITE_SPACE + text;
    }
}
