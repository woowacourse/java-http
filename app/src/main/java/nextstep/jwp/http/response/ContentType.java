package nextstep.jwp.http.response;

import java.util.Arrays;
import nextstep.jwp.exception.InvalidFileExtensionException;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "application/js");

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
}
