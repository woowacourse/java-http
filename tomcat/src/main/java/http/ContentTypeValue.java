package http;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ContentTypeValue {
    HTML(".*\\.html(\\?.*)?$", "text/html"),
    CSS(".*\\.css(\\?.*)?$", "text/css"),
    JAVASCRIPT(".*\\.js(\\?.*)?$", "text/javascript"),
    OCTET_STREAM(".*", "application/octet-stream"),
    ;

    private static final Logger log = LoggerFactory.getLogger(ContentTypeValue.class);
    private final String pattern;
    private final String format;

    ContentTypeValue(String pattern, String format) {
        this.pattern = pattern;
        this.format = format;
    }

    public static String findFormatByPattern(final String target) {
        validateNull(target);
        return Arrays.stream(ContentTypeValue.values())
                .filter(contentTypeValue -> target.matches(contentTypeValue.pattern))
                .findFirst()
                .orElse(OCTET_STREAM)
                .format;
    }

    private static void validateNull(final String targetContentTypeValue) {
        if (targetContentTypeValue == null) {
            throw new IllegalArgumentException("targetContentType은 null일 수 없습니다");
        }
    }
}
