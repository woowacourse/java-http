package nextstep.jwp.model;

import nextstep.jwp.exception.NotFoundContentTypeException;

import java.util.Arrays;
import java.util.Objects;

public enum ContentType {

    HTML("html", "text/html"),
    CSS("css", "text/css"),
    ;

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType getExtension(String extension) {
        Objects.requireNonNull(extension);
        return Arrays.stream(values())
                .filter(content -> content.extension.equals(extension))
                .findAny()
                .orElseThrow(() -> new NotFoundContentTypeException("ContentType을 찾을 수 없습니다."));
    }

    public String getType() {
        return type;
    }
}
