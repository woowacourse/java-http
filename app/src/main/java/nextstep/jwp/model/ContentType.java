package nextstep.jwp.model;

import java.util.HashMap;
import java.util.Map;
import nextstep.jwp.exception.InvalidFileExtensionException;

public enum ContentType {
    HTML("html", "text/html; charset=UTF-8"),
    CSS("css", "text/css"),
    JS("js", "application/js"),
    ICO("ico", "image/vnd.microsoft.icon"),
    SVG("svg", "image/svg+xml");

    private static final Map<String, ContentType> CONTENT_TYPES = new HashMap<>();

    static {
        for (ContentType contentType : values()) {
            CONTENT_TYPES.put(contentType.extension, contentType);
        }
    }

    private final String extension;
    private final String type;

    ContentType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static ContentType findByExtension(String fileExtension) {
        if (noExistContentType(fileExtension)) {
            throw new InvalidFileExtensionException();
        }

        return CONTENT_TYPES.get(fileExtension);
    }

    private static boolean noExistContentType(String fileExtension) {
        return !CONTENT_TYPES.containsKey(fileExtension);
    }

    public String getType() {
        return type;
    }
}
