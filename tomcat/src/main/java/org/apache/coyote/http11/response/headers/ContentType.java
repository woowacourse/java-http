package org.apache.coyote.http11.response.headers;

import java.util.Arrays;

public enum ContentType implements ResponseHeader {
    TEXT_HTML("text/css", ".html"),
    TEXT_CSS("text/html", ".css"),
    APPLICATION_JAVASCRIPT("application/javascript", ".js"),
    TEXT_PLAIN("text/plain", "");

    private final String type;
    private final String extension;

    ContentType(String type, String extension) {
        this.type = type;
        this.extension = extension;
    }

    public static ContentType findWithExtension(String path) {
        return Arrays.stream(values())
                .filter(value -> path.endsWith(value.extension))
                .findAny()
                .orElse(TEXT_PLAIN);
    }

    @Override
    public String getAsString() {
        return "Content-Type: " + type;
    }
}
