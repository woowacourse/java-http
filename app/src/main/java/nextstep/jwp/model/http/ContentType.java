package nextstep.jwp.model.http;

import java.util.Arrays;
import java.util.Optional;

public enum ContentType {
    CSS("text/css;charset=utf-8", ".css"),
    HTML("text/html;charset=utf-8", ".html"),
    JS("application/javascript;charset=utf-8", ".js"),
    ICON("image/x-icon;charset=utf-8", ".ico");

    protected static final String NAME = "Content-Type: ";
    private String value;
    private String suffix;

    ContentType(String value, String suffix) {
        this.value = value;
        this.suffix = suffix;
    }

    public String value() {
        return value;
    }

    public static Optional<ContentType> of(String url) {
        return Arrays.stream(values())
                .filter(type -> url.endsWith(type.suffix))
                .findAny();
    }
}
