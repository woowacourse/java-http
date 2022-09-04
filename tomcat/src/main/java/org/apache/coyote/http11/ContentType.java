package org.apache.coyote.http11;

import java.util.Arrays;
import java.util.function.Predicate;

public enum ContentType {
    TEXT_PLAIN("Content-Type: text/plain;charset=utf-8",
            fileSource -> fileSource.equals("/") || fileSource.equals("/favicon.ico")),

    TEXT_JS("Content-Type: text/js;charset=utf-8",
            fileSource -> fileSource.contains(".js")),

    TEXT_CSS("Content-Type: text/css;charset=utf-8",
            fileSource -> fileSource.contains(".css")),

    TEXT_HTML("Content-Type: text/html;charset=utf-8",
            fileSource -> fileSource.contains(".html") || fileSource.contains("login")),
    ;

    private final String value;
    private final Predicate<String> suitable;

    ContentType(String value, Predicate<String> suitable) {
        this.value = value;
        this.suitable = suitable;
    }

    public static ContentType from(String fileSource) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> contentType.suitable.test(fileSource))
                .findAny()
                .orElseThrow(ContentTypeNotFoundException::new);
    }

    public String getValue() {
        return value;
    }
}
