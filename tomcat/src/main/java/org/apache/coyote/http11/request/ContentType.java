package org.apache.coyote.http11.request;

import java.util.Arrays;

public enum ContentType {

    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css;charset=utf-8", ".css"),
    JAVASCRIPT("text/javascript;charset=utf-8", ".js");

    private final String content;
    private final String fileType;

    ContentType(String content, String fileType) {
        this.content = content;
        this.fileType = fileType;
    }

    public static ContentType findByUrl(String url) {
        return Arrays.stream(values())
                .filter(contentType -> url.endsWith(contentType.fileType))
                .findFirst()
                .orElse(HTML);
    }

    public String getContent() {
        return content;
    }
}
