package org.apache.coyote.http11;

import java.util.Arrays;

public enum ContentType {
    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css; charset=utf-8"),
    JS("js", "text/javascript; charset=utf-8");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType findByPath(String path){
        return Arrays.stream(values())
            .filter(value -> path.endsWith(value.extension))
            .findFirst()
            .orElse(HTML);
    }

    public String getType() {
        return type;
    }
}
