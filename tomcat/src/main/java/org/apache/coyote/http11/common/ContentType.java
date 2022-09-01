package org.apache.coyote.http11.common;

import java.util.Arrays;
import java.util.List;
import org.apache.coyote.http11.exception.UnsupportedContentTypeException;

public enum ContentType {

    IMAGE_SVG("image/svg+xml", List.of("svg")),
    APPLICATION_JS("application/javascript", List.of("js")),
    TEXT_CSS("text/css", List.of("css")),
    TEXT_HTML("text/html;charset=utf-8", List.of("html"))
    ;

    private final String type;
    private final List<String> extensions;

    ContentType(final String type, final List<String> extensions) {
        this.type = type;
        this.extensions = extensions;
    }

    public static ContentType extension(final String extension) {
        return Arrays.stream(ContentType.values())
                .filter(v -> v.extensions.contains(extension))
                .findFirst()
                .orElseThrow(UnsupportedContentTypeException::new);
    }

    public String getType() {
        return type;
    }

    public List<String> getExtensions() {
        return extensions;
    }
}
