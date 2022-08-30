package org.apache.coyote.support;

import java.util.Arrays;
import org.apache.coyote.ContentTypeNotSupportedException;

public enum ContentType {

    TEXT_HTML_CHARSET_UTF_8("html", "text/html;charset=utf-8"),
    TEXT_CSS("css", "text/css"),
    APPLICATION_JAVASCRIPT("js", "application/javascript"),
    STRINGS("strings", "text/strings")
    ;

    private final String key;
    private final String value;

    ContentType(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static ContentType from(final String extension) {
        return Arrays.stream(values())
                .filter(value -> value.getKey().equals(extension))
                .findFirst()
                .orElseThrow(ContentTypeNotSupportedException::new);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
