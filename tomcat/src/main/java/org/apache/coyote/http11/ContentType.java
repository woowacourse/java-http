package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html") {
        @Override
        boolean match(String uri) {
            return uri.endsWith(".html");
        }
    },
    CSS("text/css") {
        @Override
        boolean match(String uri) {
            return uri.endsWith(".css");
        }
    },
    JAVASCRIPT("application/js") {
        @Override
        boolean match(String uri) {
            return uri.endsWith(".js");
        }
    };

    private final String value;

    ContentType(String value) {
        this.value = value;
    }

    abstract boolean match(String uri);

    public static ContentType from(String uri) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.match(uri))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(uri + "에 해당하는 contentType은 없습니다."));
    }

    public String value() {
        return this.value;
    }
}
