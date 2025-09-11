package org.apache.coyote.http11;

public enum HttpVersion {

    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2("HTTP/2.0"),
    UNKNOWN("UNKNOWN"),
    ;

    private final String text;

    HttpVersion(final String text) {
        this.text = text;
    }

    public static HttpVersion from(final String part) {
        if (part == null) {
            return UNKNOWN;
        }
        return switch (part) {
            case "HTTP/1.0" -> HTTP_1_0;
            case "HTTP/1.1" -> HTTP_1_1;
            case "HTTP/2" -> HTTP_2;
            default -> UNKNOWN;
        };
    }

    public String getText() {
        return text;
    }
}
