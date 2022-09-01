package nextstep.jwp.http;

import java.util.Arrays;
import java.util.Objects;
import nextstep.jwp.exception.NotFoundContentTypeException;

public enum ContentType {

    TEXT_HTML("html", "text/html"),
    TEXT_CSS("css", "text/css"),
    APPLICATION_JAVASCRIPT("js", "application/javascript"),
    IMAGE_X_ICON("ico", "image/x-icon"),
    ;

    private final String extension;
    private final String type;

    ContentType(final String extension, final String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType from(final String extension) {
        Objects.requireNonNull(extension);
        return Arrays.stream(values())
            .filter(value -> value.extension.equals(extension))
            .findAny()
            .orElseThrow(NotFoundContentTypeException::new);
    }

    public String getType() {
        return type;
    }
}
