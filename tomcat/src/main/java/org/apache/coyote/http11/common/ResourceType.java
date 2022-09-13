package org.apache.coyote.http11.common;

import static org.apache.coyote.http11.common.ContentType.IMAGE_SVG;
import static org.apache.coyote.http11.common.ContentType.TEXT_CSS;
import static org.apache.coyote.http11.common.ContentType.TEXT_HTML;
import static org.apache.coyote.http11.common.ContentType.TEXT_JAVASCRIPT;

import java.util.Arrays;

public enum ResourceType {

    HTML("html", TEXT_HTML),
    CSS("css", TEXT_CSS),
    JAVASCRIPT("js", TEXT_JAVASCRIPT),
    SVG("svg", IMAGE_SVG),
    ;

    private final String extension;
    private final ContentType contentType;

    ResourceType(final String extension, final ContentType contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String getContentType(String extension) {
        return Arrays.stream(values())
            .filter(value -> value.extension.equals(extension))
            .map(value -> value.contentType.getValue())
            .findFirst()
            .orElseGet(HTML.contentType::getValue);
    }
}
