package nextstep.jwp.common;

import java.util.Arrays;

public enum ContentType {
    HTML("text/html;charset=utf-8", ".html"),
    CSS("text/css", ".css"),
    JS("Application/javascript", ".js"),
    SVG("image/svg+xml", ".svg");

    private final String type;
    private final String extension;

    ContentType(final String type, final String extension) {
        this.type = type;
        this.extension = extension;
    }

    public String getType() {
        return type;
    }

    public String getExtension() {
        return extension;
    }

    public static boolean isSupportedType(final String uri) {
        return Arrays.stream(ContentType.values())
                .anyMatch(type -> uri.endsWith(type.getExtension()));
    }

    public static String getTypeByExtension(final String uri) {
        return Arrays.stream(ContentType.values())
                .filter(type -> uri.endsWith(type.getExtension()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(""))
                .getType();
    }
}
