package org.apache.coyote.http11.component;

import java.util.Arrays;
import org.apache.coyote.exception.UncheckedHttpException;

public enum FileExtension {

    HTML(".html", MediaType.TEXT_HTML),
    CSS(".css", MediaType.TEXT_CSS),
    JAVASCRIPT(".js", MediaType.APPLICATION_JAVASCRIPT),
    JSON(".json", MediaType.APPLICATION_JSON),
    PLAIN_TEXT(".txt", MediaType.TEXT_PLAIN);

    private final String extension;
    private final MediaType mediaType;

    FileExtension(String extension, MediaType mediaType) {
        this.extension = extension;
        this.mediaType = mediaType;
    }

    public static FileExtension from(String extension) {
        return Arrays.stream(values())
                .filter(value -> value.extension.equals(extension))
                .findFirst()
                .orElseThrow(
                        () -> new UncheckedHttpException(new IllegalArgumentException("처리할 확장자가 없습니다. 값: " + extension))
                );
    }

    public String getExtension() {
        return extension;
    }

    public String getMediaType() {
        return mediaType.getValue();
    }
}
