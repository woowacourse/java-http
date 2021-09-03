package nextstep.jwp.model.httpmessage.common;

import java.util.Arrays;
import java.util.Optional;

public enum ContentType {
    CSS("text/css", ".css"),
    HTML("text/html;charset=utf-8", ".html"),
    JS("application/javascript", ".js"),
    ICON("image/x-icon", ".ico"),
    SVG("image/svg+xml", ".svg"),
    FORM("application/x-www-form-urlencoded", ""),
    PLAIN_TEXT("text/plain", "");

    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    private final String value;
    private final String suffix;

    ContentType(String value, String suffix) {
        this.value = value;
        this.suffix = suffix;
    }

    public static Optional<ContentType> of(String url) {
        return Arrays.stream(values())
                .filter(type -> url.endsWith(type.suffix))
                .findAny();
    }

    public static Optional<ContentType> getType(String value) {
        return Arrays.stream(values())
                .filter(type -> type.value.equals(value))
                .findAny();
    }

    public String value() {
        return value;
    }

    public String suffix() {
        return suffix;
    }

    @Override
    public String toString() {
        return value;
    }
}
