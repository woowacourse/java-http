package org.apache.coyote.http11.response.header;

import java.util.Arrays;
import org.apache.coyote.http11.exception.ContentTypeNotFoundException;

public enum ContentType implements HttpResponseHeader {

    TEXT_JS("text/javascript", "text/js;charset=utf-8 "),
    TEXT_CSS("text/css", "text/css;charset=utf-8 "),
    TEXT_HTML("text/html", "text/html;charset=utf-8 "),
    TEXT_PLAIN("text/plain", "text/plain;charset=utf-8 ");

    private static final String HEADER_KEY = "Content-Type: ";

    private final String key;
    private final String value;

    ContentType(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public static ContentType findByFilePath(String key) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.key.equals(key))
                .findAny()
                .orElseThrow(ContentTypeNotFoundException::new);
    }

    public String toHeaderFormat() {
        return HEADER_KEY + value;
    }
}
