package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentTypeMapper {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "application/javascript; charset=UTF-8"),
    ICON(".ico", "image/x-icon");

    private final String extension;
    private final String contentType;

    ContentTypeMapper(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String extractContentType(String resource) {
        return Arrays.stream(values())
                .filter(contentType -> resource.endsWith(contentType.extension))
                .findAny()
                .map(contentType -> contentType.contentType)
                .orElse(HTML.contentType);
    }
}
