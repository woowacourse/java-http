package nextstep.jwp.http;

import java.util.Arrays;

public enum ContentTypeMapper {
    HTML(".html", "text/html;charset=utf-8"),
    CSS(".css", "text/css"),
    JAVASCRIPT(".js", "application/javascript; charset=UTF-8"),
    ICON(".ico", "image/x-icon");

    String extension;
    String contentType;

    ContentTypeMapper(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    public static String extractContentType(String path) {
        return Arrays.stream(values())
                .filter(contentType -> path.contains(contentType.extension))
                .findAny()
                .map(contentType -> contentType.contentType)
                .orElse(HTML.contentType);
    }
}
