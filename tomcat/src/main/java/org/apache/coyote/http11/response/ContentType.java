package org.apache.coyote.http11.response;

import java.util.Arrays;
import org.apache.coyote.exception.InvalidContentTypeException;

public enum ContentType {

    TEXT_HTML_CHARSET_UTF_8("html", "text/html;charset=utf-8"),
    TEXT_CSS("css", "text/css"),
    TEXT_JAVASCRIPT("js", "text/javascript");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType from(String type) {
        return Arrays.stream(values())
                .filter(contentType -> type.equals(contentType.getExtension()))
                .findFirst()
                .orElseThrow(InvalidContentTypeException::new);
    }

    public String getExtension() {
        return extension;
    }

    public String getType() {
        return type;
    }
}
