package org.apache.coyote.http11.response.header;

import java.util.Arrays;
import java.util.function.Predicate;
import org.apache.coyote.http11.exception.ContentTypeNotFoundException;

public enum ContentType implements HttpResponseHeader {

    TEXT_JS("text/js;charset=utf-8 ", filePath -> filePath.contains(".js")),
    TEXT_CSS("text/css;charset=utf-8 ", filePath -> filePath.contains(".css")),
    TEXT_HTML("text/html;charset=utf-8 ", filePath -> filePath.contains(".html")),
    TEXT_PLAIN("text/plain;charset=utf-8 ", filePath -> false);;

    private static final String HEADER_KEY = "Content-Type: ";

    private final String value;
    private final Predicate<String> suitable;

    ContentType(String value, Predicate<String> suitable) {
        this.value = value;
        this.suitable = suitable;
    }

    public static ContentType findByFilePath(String filePath) {
        return Arrays.stream(values())
                .filter(contentType -> contentType.suitable.test(filePath))
                .findAny()
                .orElseThrow(ContentTypeNotFoundException::new);
    }

    public String toHeaderFormat() {
        return HEADER_KEY + value;
    }
}
