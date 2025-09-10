package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.List;

import org.apache.coyote.http11.request.Path;

public enum ContentType {
    NONE("", List.of("")),
    HTML("text/html", List.of("htm", "html")),
    CSS("text/css", List.of("css")),
    JAVASCRIPT("text/javascript", List.of("js"));

    private final String type;
    private final List<String> extensions;

    ContentType(String type, List<String> extensions) {
        this.type = type;
        this.extensions = extensions;
    }

    public static ContentType fromPath(Path path) {
        String extension = path.getExtension();
        return Arrays.stream(values())
            .filter(contentType -> contentType.extensions.contains(extension))
            .findAny()
            .orElse(NONE);
    }

    public String getType() {
        return type;
    }

    public boolean isText() {
        return type.contains("text");
    }
}
