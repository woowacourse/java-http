package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    TEXT("", "text/html");

    private String lastUrl;
    private String contentType;

    ContentType(final String lastUrl, final String contentType) {
        this.lastUrl = lastUrl;
        this.contentType = contentType;
    }

    public static String findContentType(final String otherUrl) {
        return Arrays.stream(values())
                .filter(value -> otherUrl.contains(value.lastUrl))
                .findFirst()
                .map(value -> value.contentType)
                .orElseThrow(IllegalArgumentException::new);
    }
}
