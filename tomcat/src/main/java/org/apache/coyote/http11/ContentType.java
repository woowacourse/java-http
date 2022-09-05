package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html", ".html"),
    CSS("text/css", ".css"),
    JAVASCRIPT("text/javascript", ".js");

    private final String content;
    private final String fileType;

    ContentType(String content, String fileType) {
        this.content = content;
        this.fileType = fileType;
    }

    public static ContentType from(String url) {
        return Arrays.stream(values())
                .filter(contentType -> url.contains(contentType.fileType))
                .findFirst()
                .orElse(HTML);
    }

    public String getContent() {
        return content;
    }
}
