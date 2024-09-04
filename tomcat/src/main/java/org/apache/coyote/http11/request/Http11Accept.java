package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum Http11Accept implements Http11RequestHeader {
    ALL("*"),
    TEXT_HTML("text/html"),
    TEXT_CSS("text/css")
    ;

    private final String accept;

    Http11Accept(String accept) {
        this.accept = accept;
    }

    public static Http11Accept from(String accept) {
        return Arrays.stream(values())
                .filter(http11Accept -> http11Accept.accept.equals(accept))
                .findFirst()
                .orElse(null);
    }
}
