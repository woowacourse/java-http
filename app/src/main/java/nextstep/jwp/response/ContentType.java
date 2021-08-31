package nextstep.jwp.response;

import nextstep.jwp.exception.PageNotFoundError;

import java.util.Arrays;

public enum ContentType {
    HTML(".html", "text/html; charset=UTF-8"),
    CSS(".css", "text/css"),
    JS(".js", "application/javascript"),
    SVG(".svg", "image/svg+xml");

    private final String extension;
    private final String headerContentType;

    ContentType(String extension, String value) {
        this.extension = extension;
        this.headerContentType = value;
    }

    public static String getValue(String file) {
        return Arrays.stream(ContentType.values())
                .filter(contentType -> file.endsWith(contentType.getExtension()))
                .findAny().orElseThrow(PageNotFoundError::new)
                .getValue();
    }

    public String getExtension() {
        return extension;
    }

    public String getValue() {
        return headerContentType;
    }
}
