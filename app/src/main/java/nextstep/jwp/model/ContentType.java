package nextstep.jwp.model;

import java.util.Arrays;
import nextstep.jwp.exception.InvalidFileExtensionException;

public enum ContentType {
    HTML("html", "text/html; charset=UTF-8"),
    CSS("css", "text/css"),
    JS("js", "application/js"),
    ICO("ico", "image/vnd.microsoft.icon"),
    SVG("svg", "image/svg+xml");

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType findByExtension(String fileExtension) {
        return Arrays.stream(values())
            .filter(contentType -> fileExtension.equals(contentType.extension))
            .findAny()
            .orElseThrow(InvalidFileExtensionException::new);
    }

    public String getType() {
        return type;
    }
}
