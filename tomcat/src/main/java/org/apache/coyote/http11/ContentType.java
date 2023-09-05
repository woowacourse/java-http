package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    TEXT_HTML("text/html", "html"),
    CSS("text/css", "css"),
    APPLICATION_JSON("application/json", "json"),
    JAVASCRIPT("application/javascript", "js"),
    ;
    private static final String DEFAULT_UTF8 = "charset=utf-8";
    private static final String EXT_SEPARATOR = ".";

    private final String type;
    private final String ext;

    ContentType(String type, String ext) {
        this.type = type;
        this.ext = ext;
    }

    public static ContentType from(String fileName) {

        int index = fileName.indexOf(EXT_SEPARATOR);
        if (index == -1) {
            throw new IllegalArgumentException("지원하지 않는 ContentType입니다. fileName: " + fileName);
        }
        String ext = fileName.substring(index + 1);
        return Arrays.stream(values())
                .filter(type -> type.ext.equalsIgnoreCase(ext))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 ContentType입니다. fileName: " + ext));
    }

    public String getType() {
        return String.format("%s;%s", type, DEFAULT_UTF8);
    }
}
