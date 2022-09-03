package org.apache.coyote.http11.header;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/js"),
    UTF_8("utf-8", "charset=utf-8");

    private final String key;
    private final String value;

    ContentType(final String key, final String value) {
        this.key = key;
        this.value = value;
    }

    public static String of(final String key) {
        return Arrays.stream(ContentType.values())
                .filter(it -> it.key.equals(key))
                .findFirst()
                .map(ContentType::getValue)
                .orElseThrow(() -> new NoSuchElementException("해당하는 ContentType이 없습니다. " + key));
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}
