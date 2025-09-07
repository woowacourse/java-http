package http;

import java.util.Arrays;

public enum ContentTypeValue {
    HTML("text/html"),
    CSS("text/css"),
    JAVASCRIPT("text/javascript"),
    OCTET_STREAM("application/octet-stream"),
    ;

    private final String format;

    ContentTypeValue(final String format) {
        this.format = format;
    }

    public static ContentTypeValue findByTarget(final String target) {
        validateNull(target);
        return Arrays.stream(ContentTypeValue.values())
                .filter(contentTypeValue -> target.equals(contentTypeValue.format))
                .findFirst()
                .orElse(OCTET_STREAM);
    }

    private static void validateNull(final String targetContentTypeValue) {
        if (targetContentTypeValue == null) {
            throw new IllegalArgumentException("targetContentType은 null일 수 없습니다");
        }
    }

    public String getFormat() {
        return format;
    }
}
