package org.apache.coyote.http11.http.common.header;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ContentTypeValue {
    HTML(".*\\.html$", "text/html"),
    CSS(".*\\.css$", "text/css"),
    JAVASCRIPT(".*\\.js$", "text/javascript"),
    ;

    private static final Logger log = LoggerFactory.getLogger(ContentTypeValue.class);
    private final String pattern;
    private final String format;

    ContentTypeValue(String pattern, String format) {
        this.pattern = pattern;
        this.format = format;
    }

    public static String findFormatByPattern(String target) {
        String test = Arrays.stream(ContentTypeValue.values())
                .filter(contentTypeValue -> target.matches(contentTypeValue.pattern))
                .findAny()
                .orElse(HTML)
                .format;
        log.info("test: {}, target: {}", test, target);
        return test;
    }
}
