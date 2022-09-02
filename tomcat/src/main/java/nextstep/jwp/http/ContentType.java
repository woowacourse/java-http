package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    TEXT("", "text/html");

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
