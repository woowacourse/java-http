package org.apache.coyote.http11.common.header;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public enum ContentTypeValue {

    TEXT_HTML("text/html", StandardCharsets.UTF_8),
    TEXT_CSS("text/css", StandardCharsets.UTF_8);

    private static final String SEPARATOR = ";";
    private static final String CHARSET = "charset";
    private static final String CHARSET_KEY_VALUE_DELIMITER = "=";
    private static final String CHARSET_KEY_WITH_DELIMITER = CHARSET + CHARSET_KEY_VALUE_DELIMITER;

    private final String type;
    private final Charset charset;

    ContentTypeValue(final String type, final Charset charset) {
        this.type = type;
        this.charset = charset;
    }

    public String convertToString() {
        return new StringBuilder().append(type).append(SEPARATOR)
                                  .append(CHARSET_KEY_WITH_DELIMITER).append(charset.name().toLowerCase())
                                  .toString();
    }
}
