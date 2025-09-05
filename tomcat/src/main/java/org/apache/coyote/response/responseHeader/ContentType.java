package org.apache.coyote.response.responseHeader;

import java.util.Arrays;
import java.util.Optional;

public enum ContentType {
    HTML("text/html", "html"),
    CSS("text/css", "css"),
    JS("text/javascript", "js"),
    PLAIN("text/plain", "txt"),
    IMAGE("image/png", "png")
    ;

    private final String contentType;
    private final String extension;

    ContentType(final String contentType, final String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isImage(){
        return extension.equals(IMAGE.extension);
    }

    public static Optional<ContentType> findContentType(String extension) {
        return Arrays.stream(ContentType.values())
                .filter(bodyType -> bodyType.extension.equals(extension))
                .findAny();
    }
}
