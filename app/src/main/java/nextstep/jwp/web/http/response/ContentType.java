package nextstep.jwp.web.http.response;

import java.util.Arrays;

public enum ContentType {
    PLAIN("text/plain;charset=utf-8", ".txt"),
    CSS("text/css;charset=utf-8", ".css"),
    HTML("text/html;charset=utf-8", ".html"),
    JS("application/javascript;charset=utf-8", ".js"),
    ICON("image/x-icon;charset=utf-8", ".ico"),
    SVG("image/svg+xml;charset=utf-8", ".svg");

    private final String value;
    private final String extension;

    ContentType(String value, String extension) {
        this.value = value;
        this.extension = extension;
    }

    public static ContentType findContentType(String extension) {
        return Arrays.stream(ContentType.values())
            .filter(contentType -> contentType.extension.equals(extension))
            .findAny()
            .orElse(ContentType.PLAIN);
    }

    public String getValue() {
        return value;
    }

    public String getExtension() {
        return this.extension;
    }
}
