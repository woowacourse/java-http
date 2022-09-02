package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html;charset=utf-8"),
    CSS("css", "text/css;charset=utf-8"),
    TEXT("", "text/html;charset=utf-8");

    private String extension;
    private String contentType;

    ContentType(final String extension, final String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String findContentType(final String otherUrl) {
        return Arrays.stream(values())
                .filter(value -> otherUrl.contains(value.extension))
                .findFirst()
                .map(value -> value.contentType)
                .orElseThrow(IllegalArgumentException::new);
    }
}
