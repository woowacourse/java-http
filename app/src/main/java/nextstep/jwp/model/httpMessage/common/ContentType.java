package nextstep.jwp.model.httpMessage.common;

import java.util.Arrays;
import java.util.Optional;

public enum ContentType {
    CSS("text/css", ".css"),
    HTML("text/html;charset=utf-8", ".html"),
    JS("application/javascript", ".js"),
    ICON("image/x-icon", ".ico"),
    FORM("application/x-www-form-urlencoded", "");

    public static final String NAME = "Content-Type";
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
}
